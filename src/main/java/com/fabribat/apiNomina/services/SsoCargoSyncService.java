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

import com.fabribat.apiNomina.entities.rrhh.BkpCargo;
import com.fabribat.apiNomina.entities.rrhh.RefCargo;
import com.fabribat.apiNomina.entities.security.RefCargoAlt;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.repositories.rrhh.BkpCargoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCargoRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;
import com.fabribat.apiNomina.repositories.security.RefCargoRepositoryAlt;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;


@Service
public class SsoCargoSyncService {

	private static final Logger log = LoggerFactory.getLogger(SsoCargoSyncService.class);

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private OrpheusSoapClient orpheusSoapClient;

	@Autowired
	private RefCargoRepository cargoRepo;

	@Autowired
	private RefCargoRepositoryAlt cargoRepoAlt;

	@Autowired
	private BkpCargoRepository bkpCargoRepo;

	@Autowired
	private RefUsuarioRepository usuarioRepo;

	@Autowired
	private SincronizacionLogRepository syncLogRepo;

	@Autowired
	private SsoDepartamentoSyncService deptoSyncService;

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

	@Transactional
	public List<Short> refrescarCargosAlt() {
		log.info("Refrescando tabla Alt de Cargos según presencia de colaboradores activos...");
		List<Short> cargosEnUso = usuarioRepo.obtenerCodigosCargosDeUsuariosActivos();
		List<RefCargo> originales = cargoRepo.findAll();
		List<Short> modificados = new ArrayList<>();

		for (RefCargo orig : originales) {
			Optional<RefCargoAlt> altOpt = cargoRepoAlt.findById(orig.getCodCargo());

			boolean tieneEmpleados = cargosEnUso.contains(orig.getCodCargo());
			String estadoCalculado = tieneEmpleados ? "A" : "I";

			// Sanitización defensiva
			String nomCargoVal = (orig.getNomCargo() != null && !orig.getNomCargo().trim().isEmpty())
					? orig.getNomCargo()
					: "CARGO " + orig.getCodCargo();

			Short codDeptoVal = (orig.getCodDepartamento() != null) ? orig.getCodDepartamento() : (short) 1;

			if (altOpt.isEmpty()) {
				RefCargoAlt nuevoAlt = new RefCargoAlt();
				nuevoAlt.setCodCargo(orig.getCodCargo());
				nuevoAlt.setNomCargo(nomCargoVal);
				nuevoAlt.setCodDepartamento(codDeptoVal);
				nuevoAlt.setEstCargo(estadoCalculado);

				cargoRepoAlt.save(nuevoAlt);
				log.info("Nuevo cargo clonado en Alt: {} (Estado: {})", orig.getCodCargo(), estadoCalculado);
				if ("A".equals(estadoCalculado)) {
					modificados.add(orig.getCodCargo());
				}
			} else {
				RefCargoAlt alt = altOpt.get();
				boolean cambiado = false;

				if (!estadoCalculado.equals(alt.getEstCargo())) {
					alt.setEstCargo(estadoCalculado);
					cambiado = true;
					log.info("Cargo {} cambió a estado '{}' (Empleados activos: {})", orig.getCodCargo(), estadoCalculado, tieneEmpleados);
				}

				if (!Objects.equals(nomCargoVal, alt.getNomCargo())) {
					alt.setNomCargo(nomCargoVal);
					cambiado = true;
				}

				if (!Objects.equals(codDeptoVal, alt.getCodDepartamento())) {
					alt.setCodDepartamento(codDeptoVal);
					cambiado = true;
				}

				if (cambiado) {
					cargoRepoAlt.save(alt);
					modificados.add(alt.getCodCargo());
				}
			}
		}
		return modificados;
	}

	public String sincronizarCargoAlt(String codCargo, boolean forzar) {
		Optional<RefCargoAlt> cargoOpt = cargoRepoAlt.findById(Short.parseShort(codCargo));

		if (cargoOpt.isEmpty()) {
			return "ERROR: Cargo no encontrado en BD Proveedor con código " + codCargo;
		}

		RefCargoAlt cargo = cargoOpt.get();
		String codigoStr = String.valueOf(cargo.getCodCargo());

		String codAreaVal = deptoSyncService.obtenerCodAreaDesdeDepartamento(cargo.getCodDepartamento());

		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", codigoStr);
		payload.put("nombre", cargo.getNomCargo());
		payload.put("departamento", codAreaVal);

		String estado = (cargo.getEstCargo() != null && cargo.getEstCargo().equals("A")) ? "A" : "I";
		payload.put("status", estado);
		payload.put("tipo", "564");

		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("CARGO", codigoStr, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = orpheusClient.setCargo(payload);
		registrarSincronizacion("CARGO", codigoStr, hash, respuesta);
		return respuesta;
	}

	public Map<String, Object> sincronizarTodosLosCargosAlt(boolean soloModificados) {
		refrescarCargosAlt();
		List<RefCargoAlt> cargos = cargoRepoAlt.findByEstCargo("A");
		int total = cargos.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefCargoAlt c : cargos) {
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			String res = sincronizarCargoAlt(String.valueOf(c.getCodCargo()), !soloModificados);
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

	public void procesarNovedadesCargos() {
		SincronizacionLog tracker = syncLogRepo.findByTipoEntidadAndCodigoEntidad("TRACKER", "BKP_CARGO")
				.orElseGet(() -> crearNuevoTracker("BKP_CARGO"));

		Long ultimoCodigo = Long.parseLong(tracker.getResultado());
		List<BkpCargo> novedades = bkpCargoRepo.findByCambCodigoGreaterThanOrderByCambCodigoAsc(ultimoCodigo);

		if (!novedades.isEmpty()) {
			log.info("Se encontraron {} novedades en bkp_cargo.", novedades.size());
			for (BkpCargo novedad : novedades) {
				sincronizarCargoAlt(String.valueOf(novedad.getCodCargo()), false);
				ultimoCodigo = (long) novedad.getCambCodigo();
			}

			tracker.setResultado(String.valueOf(ultimoCodigo));
			tracker.setFechaUltimoSync(LocalDateTime.now());
			syncLogRepo.save(tracker);
		} else {
			log.info("No hay nuevas actualizaciones de cargos en bkp_cargo.");
		}
	}

	public List<Map<String, Object>> obtenerTodosLosCargos() {
		List<RefCargo> cargos = cargoRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();

		for (RefCargo c : cargos) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", c.getCodCargo());
			item.put("nombre", c.getNomCargo());
			item.put("estado", c.getEstCargo());
			item.put("codigoDepartamento", c.getCodDepartamento());
			item.put("codigoNivel", c.getCodNivel());
			item.put("tipo", c.getTipCargo());
			item.put("valor", c.getValCargo());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerCargoPorCodigo(Long codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefCargo> opt = cargoRepo.findById(codigo.shortValue());

		if (opt.isEmpty()) {
			item.put("error", "Cargo no encontrado con código " + codigo);
			return item;
		}

		RefCargo c = opt.get();
		item.put("codigo", c.getCodCargo());
		item.put("nombre", c.getNomCargo());
		item.put("estado", c.getEstCargo());
		item.put("codigoDepartamento", c.getCodDepartamento());
		item.put("codigoNivel", c.getCodNivel());
		item.put("codigoRiesgo", c.getCodRiescargo());
		item.put("codigoRol", c.getCodRol());
		item.put("codigoSectorial", c.getCodSectorial());
		item.put("codigoEmprcargo", c.getCodEmprcargo());
		item.put("conCargo", c.getConCargo());
		item.put("criCargo", c.getCriCargo());
		item.put("insCargo", c.getInsCargo());
		item.put("tipo", c.getTipCargo());
		item.put("tipoEmba", c.getTipEmbacargo());
		item.put("valor", c.getValCargo());
		return item;
	}

	public Map<String, Object> eliminarCargosInactivosSoap() {
		List<RefCargoAlt> inactivos = cargoRepoAlt.findByEstCargoIn(List.of("I", "X"));

		int total = inactivos.size();
		int procesados = 0;
		int errores = 0;

		for (RefCargoAlt c : inactivos) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			String codigoStr = String.valueOf(c.getCodCargo());

			String res = orpheusSoapClient.eliminaPuesto(114, codigoStr);

			if ("1".equals(res) || (res != null && res.contains(">1<"))) {
				procesados++;
				registrarSincronizacion("CARGOe", codigoStr, "ELIMINADO", res);
			} else {
				errores++;
				registrarSincronizacion("CARGOe E", codigoStr, "ERROR", res);
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