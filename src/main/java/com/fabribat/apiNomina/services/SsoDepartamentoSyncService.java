package com.fabribat.apiNomina.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.fabribat.apiNomina.entities.rrhh.BkpDepartamento;
import com.fabribat.apiNomina.entities.rrhh.RefDepartamento;
import com.fabribat.apiNomina.entities.security.RefDepartamentoAlt;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.repositories.rrhh.BkpDepartamentoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefDepartamentoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;
import com.fabribat.apiNomina.repositories.security.RefDepartamentoRepositoryAlt;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;

@Service
public class SsoDepartamentoSyncService {

	private static final Logger log = LoggerFactory.getLogger(SsoDepartamentoSyncService.class);

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private OrpheusSoapClient orpheusSoapClient;

	@Autowired
	private RefDepartamentoRepository departamentoRepo;

	@Autowired
	private RefDepartamentoRepositoryAlt departamentoRepoAlt;

	@Autowired
	private BkpDepartamentoRepository bkpDepartamentoRepo;

	@Autowired
	private RefUsuarioRepository usuarioRepo;

	@Autowired
	private SincronizacionLogRepository syncLogRepo;

	@Autowired
	private SsoAreaSyncService areaSyncService;

	private String generarHash(Map<String, Object> payload) {
		try {
			return DigestUtils.md5DigestAsHex(payload.toString().getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			return String.valueOf(payload.hashCode());
		}
	}

	private boolean esRegistroModificado(String tipoEntidad, String codigo, String nuevoHash) {
		Optional<SincronizacionLog> logOpt = syncLogRepo.findByTipoEntidadAndCodigoEntidad(tipoEntidad, codigo);
		if (logOpt.isEmpty()) {
			return true;
		}
		return !nuevoHash.equals(logOpt.get().getHashContenido());
	}

	private void registrarSincronizacion(String tipoEntidad, String codigo, String hash, String resultado) {
		Optional<SincronizacionLog> logOpt = syncLogRepo.findByTipoEntidadAndCodigoEntidad(tipoEntidad, codigo);
		SincronizacionLog logEntity = logOpt.orElse(new SincronizacionLog());
		logEntity.setTipoEntidad(tipoEntidad);
		logEntity.setCodigoEntidad(codigo);
		logEntity.setHashContenido(hash);
		logEntity.setFechaUltimoSync(LocalDateTime.now());

		if (resultado != null && resultado.length() > 250) {
			resultado = resultado.substring(0, 245) + "...";
		}

		logEntity.setResultado(resultado);
		syncLogRepo.save(logEntity);
	}

	public String obtenerCodAreaDesdeDepartamento(Short codDepartamento) {
		if (codDepartamento == null || codDepartamento == 0 || codDepartamento == -1) {
			return "1";
		}

		Optional<RefDepartamentoAlt> deptoOpt = departamentoRepoAlt.findById(codDepartamento);
		if (deptoOpt.isPresent()) {
			RefDepartamentoAlt depto = deptoOpt.get();
			if (depto.getCodArea() != 0) {
				return String.valueOf(depto.getCodArea());
			}
		}
		return "24";
	}

	@Transactional
	public void refrescarDepartamentosAlt() {
		log.info("Refrescando tabla Alt de Departamentos según presencia de colaboradores activos...");
		List<Short> deptosEnUso = usuarioRepo.obtenerCodigosDepartamentosDeUsuariosActivos();
		List<RefDepartamento> originales = departamentoRepo.findAll();

		for (RefDepartamento orig : originales) {
			Optional<RefDepartamentoAlt> altOpt = departamentoRepoAlt.findById(orig.getCodDepartamento());

			boolean tieneEmpleados = deptosEnUso.contains(orig.getCodDepartamento());
			String estadoCalculado = tieneEmpleados ? "A" : "I";

			// Asegura un ideDepartamento válido (no nulo) para evitar errores NOT NULL en MySQL
			String ideDeptoVal = (orig.getIdeDepartamento() != null && !orig.getIdeDepartamento().trim().isEmpty())
					? orig.getIdeDepartamento()
					: String.valueOf(orig.getCodDepartamento());

			if (altOpt.isEmpty()) {
				RefDepartamentoAlt nuevoAlt = new RefDepartamentoAlt();
				nuevoAlt.setCodDepartamento(orig.getCodDepartamento());
				nuevoAlt.setNomDepartamento(orig.getNomDepartamento());
				nuevoAlt.setCodArea(orig.getCodArea());
				nuevoAlt.setEstDepartamento(estadoCalculado);
				nuevoAlt.setIdeDepartamento(ideDeptoVal); // 👈 FIX: Asignación de campo obligatorio
				nuevoAlt.setCodEmpresa(orig.getCodEmpresa());
				nuevoAlt.setDesDepartamento(orig.getDesDepartamento());
				nuevoAlt.setTipDepartamento(orig.getTipDepartamento());
				nuevoAlt.setUsrGerentecost(orig.getUsrGerentecost());
				nuevoAlt.setUsrGerentesier(orig.getUsrGerentesier());

				departamentoRepoAlt.save(nuevoAlt);
				log.info("Nuevo departamento clonado en Alt: {} (Estado: {})", orig.getCodDepartamento(), estadoCalculado);
			} else {
				RefDepartamentoAlt alt = altOpt.get();
				boolean cambiado = false;

				if (!estadoCalculado.equals(alt.getEstDepartamento())) {
					alt.setEstDepartamento(estadoCalculado);
					cambiado = true;
					log.info("Departamento {} cambió a estado '{}' (Empleados activos: {})", orig.getCodDepartamento(), estadoCalculado, tieneEmpleados);
				}

				if (orig.getNomDepartamento() != null && !orig.getNomDepartamento().equals(alt.getNomDepartamento())) {
					alt.setNomDepartamento(orig.getNomDepartamento());
					cambiado = true;
				}

				if (!Objects.equals(orig.getCodArea(), alt.getCodArea())) {
					alt.setCodArea(orig.getCodArea());
					cambiado = true;
				}

				if (!Objects.equals(ideDeptoVal, alt.getIdeDepartamento())) {
					alt.setIdeDepartamento(ideDeptoVal);
					cambiado = true;
				}

				if (cambiado) {
					departamentoRepoAlt.save(alt);
				}
			}
		}
	}

	public String sincronizarDepartamentoAlt(String codDepartamento, boolean forzar) {
		Optional<RefDepartamentoAlt> deptoOpt = departamentoRepoAlt.findById(Short.parseShort(codDepartamento));

		if (deptoOpt.isEmpty()) {
			return "ERROR: Departamento no encontrado en BD Proveedor con código " + codDepartamento;
		}

		RefDepartamentoAlt depto = deptoOpt.get();
		String codigoStr = String.valueOf(depto.getCodDepartamento());

		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", codigoStr);
		payload.put("nombre", depto.getNomDepartamento());

		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("DEPARTAMENTO", codigoStr, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = orpheusClient.setDepartamento(payload);
		registrarSincronizacion("DEPARTAMENTO", codigoStr, hash, respuesta);
		return respuesta;
	}

	public Map<String, Object> sincronizarTodosLosDepartamentosAlt(boolean soloModificados) {
		refrescarDepartamentosAlt();
		List<RefDepartamentoAlt> deptos = departamentoRepoAlt.findByEstDepartamento("A");
		int total = deptos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefDepartamentoAlt d : deptos) {
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			String res = sincronizarDepartamentoAlt(String.valueOf(d.getCodDepartamento()), !soloModificados);
			if (res.startsWith("SKIPPED")) {
				omitidos++;
			} else if ("TRUE".equalsIgnoreCase(res != null ? res.trim() : "")) {
				procesados++;
			} else {
				errores++;
			}
		}

		Map<String, Object> resumen = new HashMap<>();
		resumen.put("total", total);
		resumen.put("procesados", procesados);
		resumen.put("omitidos", omitidos);
		resumen.put("errores", errores);
		return resumen;
	}

	public void procesarNovedadesDepartamentos() {
		SincronizacionLog tracker = syncLogRepo.findByTipoEntidadAndCodigoEntidad("TRACKER", "BKP_DEPARTAMENTO")
				.orElseGet(() -> crearNuevoTracker("BKP_DEPARTAMENTO"));

		Long ultimoCodigo = Long.parseLong(tracker.getResultado());
		List<BkpDepartamento> novedades = bkpDepartamentoRepo.findByCambCodigoGreaterThanOrderByCambCodigoAsc(ultimoCodigo);

		if (!novedades.isEmpty()) {
			log.info("Se encontraron {} novedades en bkp_departamento.", novedades.size());
			for (BkpDepartamento novedad : novedades) {
				String codAreaVal = obtenerCodAreaDesdeDepartamento(novedad.getCodDepartamento());
				areaSyncService.sincronizarAreaAlt(Short.parseShort(codAreaVal), false);
				ultimoCodigo = (long) novedad.getCambCodigo();
			}

			tracker.setResultado(String.valueOf(ultimoCodigo));
			tracker.setFechaUltimoSync(LocalDateTime.now());
			syncLogRepo.save(tracker);
		} else {
			log.info("No hay nuevas actualizaciones de departamentos en bkp_departamento.");
		}
	}

	public List<Map<String, Object>> obtenerTodosLosDepartamentos() {
		List<RefDepartamento> departamentos = departamentoRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();

		for (RefDepartamento d : departamentos) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", d.getCodDepartamento());
			item.put("nombre", d.getNomDepartamento());
			item.put("estado", d.getEstDepartamento());
			item.put("descripcion", d.getDesDepartamento());
			item.put("codigoEmpresa", d.getCodEmpresa());
			item.put("codigoArea", d.getCodArea());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerDepartamentoPorCodigo(String codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefDepartamento> opt = departamentoRepo.findById(Short.parseShort(codigo));

		if (opt.isEmpty()) {
			item.put("error", "Departamento no encontrado con código " + codigo);
			return item;
		}

		RefDepartamento d = opt.get();
		item.put("codigo", d.getCodDepartamento());
		item.put("nombre", d.getNomDepartamento());
		item.put("estado", d.getEstDepartamento());
		item.put("descripcion", d.getDesDepartamento());
		item.put("codigoEmpresa", d.getCodEmpresa());
		item.put("codigoArea", d.getCodArea());
		item.put("ideDepartamento", d.getIdeDepartamento());
		item.put("objEspedepartamento", d.getObjEspedepartamento());
		item.put("objEstrdepartamento", d.getObjEstrdepartamento());
		item.put("orgDepartamento", d.getOrgDepartamento());
		item.put("priDepartamento", d.getPriDepartamento());
		item.put("resDepartamento", d.getResDepartamento());
		item.put("tipDepartamento", d.getTipDepartamento());
		item.put("usrGerentecost", d.getUsrGerentecost());
		item.put("usrGerentesier", d.getUsrGerentesier());
		return item;
	}

	public Map<String, Object> eliminarDepartamentosInactivosSoap() {
		List<RefDepartamentoAlt> inactivos = departamentoRepoAlt.findByEstDepartamentoIn(List.of("I", "X"));

		int total = inactivos.size();
		int procesados = 0;
		int errores = 0;

		for (RefDepartamentoAlt d : inactivos) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			String codigoStr = String.valueOf(d.getCodDepartamento());

			String res = orpheusSoapClient.eliminaDepartamento(114, codigoStr);

			if ("1".equals(res) || (res != null && res.contains(">1<"))) {
				procesados++;
				registrarSincronizacion("DEPARTAMENTOe", codigoStr, "ELIMINADO", res);
			} else {
				errores++;
				registrarSincronizacion("DEPARTAMENTOe E", codigoStr, "ERROR", res);
			}
		}

		Map<String, Object> resumen = new HashMap<>();
		resumen.put("total_inactivos_db", total);
		resumen.put("eliminados_exitosos", procesados);
		resumen.put("errores_o_en_uso", errores);
		return resumen;
	}

	private SincronizacionLog crearNuevoTracker(String codigoEntidad) {
		SincronizacionLog nuevo = new SincronizacionLog();
		nuevo.setTipoEntidad("TRACKER");
		nuevo.setCodigoEntidad(codigoEntidad);
		nuevo.setHashContenido("N/A");
		nuevo.setResultado("0");
		nuevo.setFechaUltimoSync(LocalDateTime.now());
		return nuevo;
	}
}