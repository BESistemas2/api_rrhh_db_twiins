package com.fabribat.apiNomina.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.fabribat.apiNomina.entities.rrhh.NomiRelUsuariocentrocosto;
import com.fabribat.apiNomina.entities.rrhh.RefCanton;
import com.fabribat.apiNomina.entities.rrhh.RefCiudad;
import com.fabribat.apiNomina.entities.rrhh.RefProvincia;
import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import com.fabribat.apiNomina.repositories.rrhh.RefCantonRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefCiudadRepository;
import com.fabribat.apiNomina.repositories.rrhh.RefProvinciaRepository;
import com.fabribat.apiNomina.repositories.security.SincronizacionLogRepository;

@Service
public class SsoSincronizacionServiceAlt {

	private static final Logger log = LoggerFactory.getLogger(SsoSincronizacionServiceAlt.class);

	@Autowired
	private OrpheusRestClient orpheusClient;

	@Autowired
	private RefProvinciaRepository provinciaRepo;

	@Autowired
	private RefCiudadRepository ciudadRepo;

	@Autowired
	private RefCantonRepository cantonRepo;

	@Autowired
	private SincronizacionLogRepository syncLogRepo;

	// Sub-servicios especializados por dominio
	@Autowired
	private SsoAreaSyncService areaSyncService;

	@Autowired
	private SsoDepartamentoSyncService deptoSyncService;

	@Autowired
	private SsoCargoSyncService cargoSyncService;

	@Autowired
	private SsoEmpleadoSyncService empleadoSyncService;

	@Autowired
	private SsoCentroCostoSyncService centroCostoSyncService;

	@org.springframework.beans.factory.annotation.Value("${sync.automatica.alt.habilitada:true}")
	private boolean automatizacionHabilitada;

	// =========================================================================
	// MÉTODOS DELEGADOS PARA MANTENER COMPATIBILIDAD CON CONTROLADORES
	// =========================================================================

	public List<Short> refrescarAreasAlt() {
		return areaSyncService.refrescarAreasAlt();
	}

	public void refrescarDepartamentosAlt() {
		deptoSyncService.refrescarDepartamentosAlt();
	}

	public List<Short> refrescarCargosAlt() {
		return cargoSyncService.refrescarCargosAlt();
	}

	public String sincronizarAreaAlt(Short codArea, boolean forzar) {
		return areaSyncService.sincronizarAreaAlt(codArea, forzar);
	}

	public Map<String, Object> sincronizarTodasLasAreasAlt(boolean soloModificados) {
		return areaSyncService.sincronizarTodasLasAreasAlt(soloModificados);
	}

	public String sincronizarDepartamentoAlt(String codDepartamento) {
		return deptoSyncService.sincronizarDepartamentoAlt(codDepartamento, true);
	}

	public String sincronizarDepartamentoAlt(String codDepartamento, boolean forzar) {
		return deptoSyncService.sincronizarDepartamentoAlt(codDepartamento, forzar);
	}

	public Map<String, Object> sincronizarTodosLosDepartamentosAlt(boolean soloModificados) {
		return deptoSyncService.sincronizarTodosLosDepartamentosAlt(soloModificados);
	}

	public String sincronizarCargoAlt(String codCargo) {
		return cargoSyncService.sincronizarCargoAlt(codCargo, true);
	}

	public String sincronizarCargoAlt(String codCargo, boolean forzar) {
		return cargoSyncService.sincronizarCargoAlt(codCargo, forzar);
	}

	public Map<String, Object> sincronizarTodosLosCargosAlt(boolean soloModificados) {
		return cargoSyncService.sincronizarTodosLosCargosAlt(soloModificados);
	}

	public String sincronizarEmpleado(String cedula) {
		return empleadoSyncService.sincronizarEmpleado(cedula, true);
	}

	public String sincronizarEmpleado(String cedula, boolean forzar) {
		return empleadoSyncService.sincronizarEmpleado(cedula, forzar);
	}

	public Map<String, Object> sincronizarTodosLosEmpleados(boolean soloModificados) {
		return empleadoSyncService.sincronizarTodosLosEmpleados(soloModificados);
	}

	public String sincronizarCentrodecosto(String codCentrodecosto) {
		return centroCostoSyncService.sincronizarCentrodecosto(codCentrodecosto);
	}

	public String sincronizarCentrodecosto(String codCentrodecosto, boolean forzar) {
		return centroCostoSyncService.sincronizarCentrodecosto(codCentrodecosto, forzar);
	}

	public Map<String, Object> sincronizarTodosLosCentrosdecosto(boolean soloModificados) {
		return centroCostoSyncService.sincronizarTodosLosCentrosdecosto(soloModificados);
	}

	public List<NomiRelUsuariocentrocosto> obtenerCentrosCostoPorUsuario(String usrUsuario) {
		return centroCostoSyncService.obtenerCentrosCostoPorUsuario(usrUsuario);
	}

	public List<Map<String, Object>> obtenerTodosLosCentrosdecosto() {
		return centroCostoSyncService.obtenerTodosLosCentrosdecosto();
	}

	public Map<String, Object> obtenerCentrodecostoPorCodigo(String codigo) {
		return centroCostoSyncService.obtenerCentrodecostoPorCodigo(codigo);
	}

	public List<Map<String, Object>> obtenerTodasLasAreas() {
		return areaSyncService.obtenerTodasLasAreas();
	}

	public Map<String, Object> obtenerAreaPorCodigo(Short codigo) {
		return areaSyncService.obtenerAreaPorCodigo(codigo);
	}

	public List<Map<String, Object>> obtenerTodosLosDepartamentos() {
		return deptoSyncService.obtenerTodosLosDepartamentos();
	}

	public Map<String, Object> obtenerDepartamentoPorCodigo(String codigo) {
		return deptoSyncService.obtenerDepartamentoPorCodigo(codigo);
	}

	public List<Map<String, Object>> obtenerTodosLosCargos() {
		return cargoSyncService.obtenerTodosLosCargos();
	}

	public Map<String, Object> obtenerCargoPorCodigo(Long codigo) {
		return cargoSyncService.obtenerCargoPorCodigo(codigo);
	}

	public Map<String, Object> obtenerPayloadEmpleado(String cedula) {
		return empleadoSyncService.obtenerPayloadEmpleado(cedula);
	}

	public List<Map<String, Object>> obtenerPayloadTodosLosEmpleados() {
		return empleadoSyncService.obtenerPayloadTodosLosEmpleados();
	}

	public Map<String, Object> eliminarDepartamentosInactivosSoap() {
		return deptoSyncService.eliminarDepartamentosInactivosSoap();
	}

	public Map<String, Object> eliminarCargosInactivosSoap() {
		return cargoSyncService.eliminarCargosInactivosSoap();
	}

	// =========================================================================
	// 1. SINCRONIZAR SUCURSAL
	// =========================================================================

	public String sincronizarSucursalPorDefecto() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("codigo", "001");
		payload.put("nombre", "MATRIZ");
		payload.put("provincia", "17");
		payload.put("ciudad", "1");
		payload.put("status", "A");

		String hash = generarHash(payload);
		String respuesta = orpheusClient.setSucursal(payload);
		registrarSincronizacion("SUCURSAL", "001", hash, respuesta);
		return respuesta;
	}

	// =========================================================================
	// SINCRONIZAR TODO MASIVO
	// =========================================================================

	public Map<String, Object> sincronizarTodoMasivo(boolean soloModificados) {
		Map<String, Object> resumenGeneral = new HashMap<>();

		deptoSyncService.refrescarDepartamentosAlt();

		String matriz = sincronizarSucursalPorDefecto();
		// Map<String, Object> deptos =
		// sincronizarTodosLosDepartamentosAlt(soloModificados);
		// Map<String, Object> deptos =
		// sincronizarTodosLosCentrosdecosto(soloModificados);
		Map<String, Object> deptos = sincronizarTodasLasAreasAlt(soloModificados);
		Map<String, Object> cargos = sincronizarTodosLosCargosAlt(soloModificados);
		Map<String, Object> empleados = sincronizarTodosLosEmpleados(soloModificados);

		resumenGeneral.put("matriz", matriz);
		resumenGeneral.put("departamentos", deptos);
		resumenGeneral.put("cargos", cargos);
		resumenGeneral.put("empleados", empleados);

		return resumenGeneral;
	}

	// =========================================================================
	// CONTROL DINÁMICO DE AUTOMATIZACIÓN EN BD SECURITY
	// =========================================================================

	/**
	 * Consulta en la BD Security si la automatización está habilitada.
	 */
	public boolean isAutomatizacionHabilitada() {
		Optional<SincronizacionLog> configOpt = syncLogRepo.findByTipoEntidadAndCodigoEntidad("CONFIG",
				"AUTO_SYNC_ALT");
		if (configOpt.isPresent()) {
			return "true".equalsIgnoreCase(configOpt.get().getResultado());
		}
		// Si aún no se ha creado el registro en BD, usa el valor de
		// application.properties por defecto
		return automatizacionHabilitada;
	}

	/**
	 * Cambia el estado de la automatización directamente en la BD Security.
	 */
	public boolean cambiarEstadoAutomatizacion(boolean habilitada) {
		Optional<SincronizacionLog> configOpt = syncLogRepo.findByTipoEntidadAndCodigoEntidad("CONFIG",
				"AUTO_SYNC_ALT");
		SincronizacionLog config = configOpt.orElseGet(() -> {
			SincronizacionLog nuevo = new SincronizacionLog();
			nuevo.setTipoEntidad("CONFIG");
			nuevo.setCodigoEntidad("AUTO_SYNC_ALT");
			nuevo.setHashContenido("N/A");
			return nuevo;
		});

		config.setResultado(String.valueOf(habilitada));
		config.setFechaUltimoSync(LocalDateTime.now());
		syncLogRepo.save(config);

		log.info("⚙️ Estado de automatización actualizado en BD Security: habilitada = {}", habilitada);
		return habilitada;
	}

	// ====================================================================
	// ORQUESTADOR CRON JOB (EJECUCIÓN CADA 5 MINUTOS)
	// ====================================================================

	@org.springframework.scheduling.annotation.Scheduled(fixedDelay = 300000)
	public void orquestadorSincronizacionAutomatica() {
		// Consulta el estado en BD Security en lugar de la variable local fija
		if (!isAutomatizacionHabilitada()) {
			log.info("⏳ Sincronización automática en pausa por configuración en BD Security.");
			return;
		}
		log.info("--- INICIANDO CICLO DE SINCRONIZACIÓN AUTOMÁTICA (DELTA + ACTIVOS) ---");

		try {
			// Paso 1: Refrescar BD Espejo local
			log.info("Paso 1: Refrescando catálogos en BD Espejo...");
			List<Short> areasModificadas = areaSyncService.refrescarAreasAlt();
			deptoSyncService.refrescarDepartamentosAlt();
			List<Short> cargosModificados = cargoSyncService.refrescarCargosAlt();

			for (Short codArea : areasModificadas) {
				areaSyncService.sincronizarAreaAlt(codArea, false);
			}
			for (Short codCargo : cargosModificados) {
				cargoSyncService.sincronizarCargoAlt(String.valueOf(codCargo), false);
			}

			// Paso 2: Procesar deltas BKP hacia Orpheus
			log.info("Paso 2: Procesando novedades incrementales hacia Orpheus...");
			areaSyncService.procesarNovedadesAreas();
			deptoSyncService.procesarNovedadesDepartamentos();
			cargoSyncService.procesarNovedadesCargos();
			empleadoSyncService.procesarNovedadesEmpleados();

			log.info("--- CICLO DE SINCRONIZACIÓN FINALIZADO EXITOSAMENTE ---");

		} catch (Exception e) {
			log.error("Error crítico durante el ciclo de sincronización automática", e);
		}
	}

	// =========================================================================
	// CONSULTA DE CATÁLOGOS SECUNDARIOS
	// =========================================================================

	public List<Map<String, Object>> obtenerTodasLasProvincias() {
		List<RefProvincia> provincias = provinciaRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();

		for (RefProvincia p : provincias) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", p.getCodProvincia());
			item.put("nombre", p.getNomProvincia());
			item.put("estado", p.getEstProvincia());
			item.put("codigoSri", p.getCodSriprovincia());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerProvinciaPorCodigo(Long codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefProvincia> opt = provinciaRepo.findById(codigo);

		if (opt.isEmpty()) {
			item.put("error", "Provincia no encontrada con código " + codigo);
			return item;
		}

		RefProvincia p = opt.get();
		item.put("codigo", p.getCodProvincia());
		item.put("nombre", p.getNomProvincia());
		item.put("estado", p.getEstProvincia());
		item.put("codigoSri", p.getCodSriprovincia());
		item.put("codigoPais", p.getCodPais());
		item.put("codigoArea", p.getCodAreaprovincia());
		return item;
	}

	public List<Map<String, Object>> obtenerTodasLasCiudades() {
		List<RefCiudad> ciudades = ciudadRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();

		for (RefCiudad c : ciudades) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", c.getCodCiudad());
			item.put("nombre", c.getNomCiudad());
			item.put("estado", c.getEstCiudad());
			item.put("codigoProvincia", c.getCodProvincia());
			item.put("codigoCanton", c.getCodCanton());
			item.put("codigoRegion", c.getCodRegion());
			item.put("codigoSri", c.getCodSriciudad());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerCiudadPorCodigo(Long codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefCiudad> opt = ciudadRepo.findById(codigo);

		if (opt.isEmpty()) {
			item.put("error", "Ciudad no encontrada con código " + codigo);
			return item;
		}

		RefCiudad c = opt.get();
		item.put("codigo", c.getCodCiudad());
		item.put("nombre", c.getNomCiudad());
		item.put("estado", c.getEstCiudad());
		item.put("codigoProvincia", c.getCodProvincia());
		item.put("codigoCanton", c.getCodCanton());
		item.put("codigoRegion", c.getCodRegion());
		item.put("codigoSri", c.getCodSriciudad());
		item.put("ideCiudad", c.getIdeCiudad());
		item.put("traCiudad", c.getTraCiudad());
		return item;
	}

	public List<Map<String, Object>> obtenerTodosLosCantones() {
		List<RefCanton> cantones = cantonRepo.findAll();
		List<Map<String, Object>> lista = new ArrayList<>();

		for (RefCanton c : cantones) {
			Map<String, Object> item = new HashMap<>();
			item.put("codigo", c.getCodCanton());
			item.put("nombre", c.getNomCanton());
			item.put("estado", c.getEstCanton());
			item.put("codigoProvincia", c.getCodProvincia());
			lista.add(item);
		}
		return lista;
	}

	public Map<String, Object> obtenerCantonPorCodigo(Short codigo) {
		Map<String, Object> item = new HashMap<>();
		Optional<RefCanton> opt = cantonRepo.findById(codigo);

		if (opt.isEmpty()) {
			item.put("error", "Canton no encontrado con código " + codigo);
			return item;
		}

		RefCanton c = opt.get();
		item.put("codigo", c.getCodCanton());
		item.put("nombre", c.getNomCanton());
		item.put("estado", c.getEstCanton());
		item.put("codigoProvincia", c.getCodProvincia());

		return item;
	}

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
}