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

import com.fabribat.apiNomina.entities.rrhh.BkpArea;
import com.fabribat.apiNomina.entities.rrhh.RefArea;
import com.fabribat.apiNomina.entities.security.RefAreaAlt;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.repositories.rrhh.BkpAreaRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefAreaRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefUsuarioRepository;
import com.fabribat.apiNomina.repositories.security.RefAreaRepositoryAlt;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;

@Service
public class SsoAreaSyncService {

	private static final Logger log = LoggerFactory.getLogger(SsoAreaSyncService.class);

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private RefAreaRepository areaRepo;

	@Autowired
	private RefAreaRepositoryAlt areaRepoAlt;

	@Autowired
	private BkpAreaRepository bkpAreaRepo;

	@Autowired
	private RefUsuarioRepository usuarioRepo;

	@Autowired
	private SincronizacionLogRepository syncLogRepo;

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
	public List<Short> refrescarAreasAlt() {
		log.info("Refrescando tabla Alt de Áreas según presencia de colaboradores activos...");
		List<Short> areasEnUso = usuarioRepo.obtenerCodigosAreasDeUsuariosActivos();
		List<RefArea> originales = areaRepo.findAll();
		List<Short> modificados = new ArrayList<>();

		for (RefArea orig : originales) {
			Optional<RefAreaAlt> altOpt = areaRepoAlt.findById(orig.getCodArea());

			boolean tieneEmpleados = areasEnUso.contains(orig.getCodArea());
			String estadoCalculado = tieneEmpleados ? "A" : "I";

			// Sanitización defensiva
			String nomAreaVal = (orig.getNomArea() != null && !orig.getNomArea().trim().isEmpty())
					? orig.getNomArea()
					: "AREA " + orig.getCodArea();

			String ideAreaVal = (orig.getIdeArea() != null && !orig.getIdeArea().trim().isEmpty())
					? orig.getIdeArea()
					: String.valueOf(orig.getCodArea());

			String tipAreaVal = (orig.getTipArea() != null) ? orig.getTipArea() : "";
			String usrGerenteVal = (orig.getUsrGerente() != null) ? orig.getUsrGerente() : "";
			Short codEmpresaVal = (orig.getCodEmpresa() != null) ? orig.getCodEmpresa() : (short) 1;

			if (altOpt.isEmpty()) {
				RefAreaAlt nuevoAlt = new RefAreaAlt();
				nuevoAlt.setCodArea(orig.getCodArea());
				nuevoAlt.setNomArea(nomAreaVal);
				nuevoAlt.setIdeArea(ideAreaVal);
				nuevoAlt.setTipArea(tipAreaVal);
				nuevoAlt.setUsrGerente(usrGerenteVal);
				nuevoAlt.setCodEmpresa(codEmpresaVal);
				nuevoAlt.setEstArea(estadoCalculado);

				areaRepoAlt.save(nuevoAlt);
				log.info("Nueva área clonada en Alt: {} (Estado: {})", orig.getCodArea(), estadoCalculado);
				if ("A".equals(estadoCalculado)) {
					modificados.add(orig.getCodArea());
				}
			} else {
				RefAreaAlt alt = altOpt.get();
				boolean cambiado = false;

				if (!estadoCalculado.equals(alt.getEstArea())) {
					alt.setEstArea(estadoCalculado);
					cambiado = true;
					log.info("Área {} cambió a estado '{}' (Empleados activos: {})", orig.getCodArea(), estadoCalculado, tieneEmpleados);
				}

				if (!Objects.equals(nomAreaVal, alt.getNomArea())) {
					alt.setNomArea(nomAreaVal);
					cambiado = true;
				}

				if (cambiado) {
					areaRepoAlt.save(alt);
					modificados.add(alt.getCodArea());
				}
			}
		}
		return modificados;
	}

	public String sincronizarAreaAlt(Short codArea, boolean forzar) {
		Optional<RefAreaAlt> areaOpt = areaRepoAlt.findById(codArea);

		if (areaOpt.isEmpty()) {
			return "ERROR: Área no encontrada en BD con código " + codArea;
		}

		RefAreaAlt area = areaOpt.get();
		String codigoStr = String.valueOf(area.getCodArea());

		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", codigoStr);
		payload.put("nombre", area.getNomArea());

		String hash = generarHash(payload);

		if (!forzar && !esRegistroModificado("DEPARTAMENTO", codigoStr, hash)) {
			return "SKIPPED: Sin cambios";
		}

		String respuesta = orpheusClient.setDepartamento(payload);
		registrarSincronizacion("DEPARTAMENTO", codigoStr, hash, respuesta);
		return respuesta;
	}

	public Map<String, Object> sincronizarTodasLasAreasAlt(boolean soloModificados) {
		refrescarAreasAlt();
		List<RefAreaAlt> areas = areaRepoAlt.findByEstArea("A");
		int total = areas.size();
		int procesados = 0;
		int omitidos = 0;
		int errores = 0;

		for (RefAreaAlt a : areas) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			String res = sincronizarAreaAlt(a.getCodArea(), !soloModificados);
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

	public void procesarNovedadesAreas() {
		SincronizacionLog tracker = syncLogRepo.findByTipoEntidadAndCodigoEntidad("TRACKER", "BKP_AREA")
				.orElseGet(() -> crearNuevoTracker("BKP_AREA"));

		Long ultimoCodigo = Long.parseLong(tracker.getResultado());
		List<BkpArea> novedades = bkpAreaRepo.findByCambCodigoGreaterThanOrderByCambCodigoAsc(ultimoCodigo);

		if (!novedades.isEmpty()) {
			log.info("Se encontraron {} novedades en bkp_area.", novedades.size());
			for (BkpArea novedad : novedades) {
				sincronizarAreaAlt(novedad.getCodArea(), false);
				ultimoCodigo = (long) novedad.getCambCodigo();
			}

			tracker.setResultado(String.valueOf(ultimoCodigo));
			tracker.setFechaUltimoSync(LocalDateTime.now());
			syncLogRepo.save(tracker);
		} else {
			log.info("No hay nuevas actualizaciones de áreas en bkp_area.");
		}
	}

	public List<Map<String, Object>> obtenerTodasLasAreas() {
		List<RefArea> areas = areaRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();

		for (RefArea a : areas) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", a.getCodArea());
			item.put("nombre", a.getNomArea());
			item.put("estado", a.getEstArea());
			item.put("ideArea", a.getIdeArea());
			item.put("codigoEmpresa", a.getCodEmpresa());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerAreaPorCodigo(Short codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefArea> opt = areaRepo.findById(codigo);

		if (opt.isEmpty()) {
			item.put("error", "Área no encontrada con código " + codigo);
			return item;
		}

		RefArea a = opt.get();
		item.put("codigo", a.getCodArea());
		item.put("nombre", a.getNomArea());
		item.put("estado", a.getEstArea());
		item.put("ideArea", a.getIdeArea());
		item.put("tipoArea", a.getTipArea());
		item.put("usrGerente", a.getUsrGerente());
		item.put("codigoEmpresa", a.getCodEmpresa());
		return item;
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