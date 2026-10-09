package com.fabribat.apiNomina.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fabribat.apiNomina.services.SsoSincronizacionServiceAlt;

@RestController
@RequestMapping("/api/v1/sso/sincronizacion-alt")
public class SsoSincronizacionControllerAlt {

	@Autowired
	private SsoSincronizacionServiceAlt syncService;

	// =========================================================================
	// ENDPOINTS ACTIVOS (PRODUCCIÓN)
	// =========================================================================

	@PostMapping("/sucursal-matriz")
	public ResponseEntity<String> syncSucursalMatriz() {
		return ResponseEntity.ok(syncService.sincronizarSucursalPorDefecto());
	}

	/**
	 * Sincroniza un Área individual. NOTA: En Orpheus, las Áreas de nuestra BD se
	 * mapean y consumen el endpoint de "Departamentos".
	 */
	@PostMapping("/area/{codArea}")
	public ResponseEntity<String> syncArea(@PathVariable Short codArea) {
		return ResponseEntity.ok(syncService.sincronizarAreaAlt(codArea, true));
	}

	@PostMapping("/cargo/{codCargo}")
	public ResponseEntity<String> syncCargo(@PathVariable String codCargo) {
		return ResponseEntity.ok(syncService.sincronizarCargoAlt(codCargo, true));
	}

	@PostMapping("/empleado/{cedula}")
	public ResponseEntity<String> syncEmpleado(@PathVariable String cedula) {
		return ResponseEntity.ok(syncService.sincronizarEmpleado(cedula, true));
	}

	// =========================================================================
	// ENDPOINTS MASIVOS ACTIVOS
	// =========================================================================

	@PostMapping("/masiva")
	public ResponseEntity<Map<String, Object>> syncMasivo(
			@RequestParam(defaultValue = "true") boolean soloModificados) {
		Map<String, Object> resumen = syncService.sincronizarTodoMasivo(soloModificados);
		return ResponseEntity.ok(resumen);
	}

	@PostMapping("/areas/masivo")
	public ResponseEntity<Map<String, Object>> syncAreasMasivo(
			@RequestParam(defaultValue = "true") boolean soloModificados) {
		Map<String, Object> resumen = syncService.sincronizarTodasLasAreasAlt(soloModificados);
		return ResponseEntity.ok(resumen);
	}

	@PostMapping("/cargos/masivo")
	public ResponseEntity<Map<String, Object>> syncCargosMasivo(
			@RequestParam(defaultValue = "true") boolean soloModificados) {
		Map<String, Object> resumen = syncService.sincronizarTodosLosCargosAlt(soloModificados);
		return ResponseEntity.ok(resumen);
	}

	@PostMapping("/empleados/masivo")
	public ResponseEntity<Map<String, Object>> syncEmpleadosMasivo(
			@RequestParam(defaultValue = "true") boolean soloModificados) {
		Map<String, Object> resumen = syncService.sincronizarTodosLosEmpleados(soloModificados);
		return ResponseEntity.ok(resumen);
	}

	// =========================================================================
	// ENDPOINTS DE ELIMINACIÓN VÍA SOAP
	// =========================================================================

	@PostMapping("/departamentos/eliminar-inactivos")
	public ResponseEntity<Map<String, Object>> eliminarDepartamentosInactivos() {
		Map<String, Object> resumen = syncService.eliminarDepartamentosInactivosSoap();
		return ResponseEntity.ok(resumen);
	}

	@PostMapping("/cargos/eliminar-inactivos")
	public ResponseEntity<Map<String, Object>> eliminarCargosInactivos() {
		Map<String, Object> resumen = syncService.eliminarCargosInactivosSoap();
		return ResponseEntity.ok(resumen);
	}

	@PostMapping("/purgar-inactivos-masivo")
	public ResponseEntity<Map<String, Object>> purgarInactivosMasivo() {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("departamentos", syncService.eliminarDepartamentosInactivosSoap());
		resultado.put("cargos", syncService.eliminarCargosInactivosSoap());
		return ResponseEntity.ok(resultado);
	}

	// =========================================================================
    // CONFIGURACIÓN Y ESTADO DE LA AUTOMATIZACIÓN ALT (BD SECURITY)
    // =========================================================================

    /**
     * Consulta el estado actual de la sincronización automática ALT.
     */
    @GetMapping("/config/auto-sync")
    public ResponseEntity<Map<String, Object>> obtenerEstadoAutoSyncAlt() {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("automatizacionAltHabilitada", syncService.isAutomatizacionHabilitada());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Activa o pausa la sincronización automática ALT.
     * POST /api/v1/sso/sincronizacion-alt/config/auto-sync?habilitada=true
     */
    @PostMapping("/config/auto-sync")
    public ResponseEntity<Map<String, Object>> cambiarEstadoAutoSyncAlt(@RequestParam boolean habilitada) {
        boolean nuevoEstado = syncService.cambiarEstadoAutomatizacion(habilitada);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Estado de sincronización ALT actualizado en BD Security");
        respuesta.put("automatizacionAltHabilitada", nuevoEstado);
        return ResponseEntity.ok(respuesta);
    }

	// =========================================================================
	// ⚠️ HISTORIAL DE ARQUITECTURA Y ENDPOINTS DESHABILITADOS ⚠️
	// =========================================================================
	/*
	 * EVOLUCIÓN DEL MAPEO HACIA EL CATÁLOGO "DEPARTAMENTO" DE ORPHEUS:
	 * -------------------------------------------------------------------------
	 * Debido a las limitaciones estructurales del proveedor externo, el concepto de
	 * "Departamento" en Orpheus sufrió mutaciones a lo largo del tiempo para
	 * adaptarse a la jerarquía real de la empresa:
	 * 
	 * - Fase 1: Se enviaba la tabla original `ref_departamento`. - Fase 2: Se
	 * intentó usar la tabla `nomi_ref_centrodecosto` como departamento. - Fase 3
	 * (ACTUAL DEFINITIVA): Se determinó que la estructura operativa real que
	 * gobierna a la organización es la tabla `ref_area`. Por lo tanto, actualmente
	 * son las ÁREAS las que se envían a la API /set_departamento.
	 * 
	 * RIESGO CRÍTICO DE CORRUPCIÓN DE DATOS: Los siguientes endpoints están
	 * COMENTADOS INTENCIONALMENTE. Si un desarrollador los descomenta y son
	 * consumidos por el Frontend o Postman, el sistema sobreescribirá el catálogo
	 * de Áreas en Orpheus con Centros de Costo o Departamentos internos. Esto
	 * corrompería la data de Empleados y Puestos (Cargos), los cuales ya están
	 * programados para mandar su código de ÁREA como su llave foránea de
	 * departamento.
	 * 
	 * Se preserva este código fuente exclusivamente con fines de documentación
	 * histórica y como respaldo de métodos internos.
	 */

	/*
	 * @PostMapping("/departamento/{codDepartamento}") public ResponseEntity<String>
	 * syncDepartamento(@PathVariable String codDepartamento) { return
	 * ResponseEntity.ok(syncService.sincronizarDepartamentoAlt(codDepartamento,
	 * true)); }
	 * 
	 * @PostMapping("/departamentos/masivo") public ResponseEntity<Map<String,
	 * Object>> syncDepartamentosMasivo(
	 * 
	 * @RequestParam(defaultValue = "true") boolean soloModificados) { Map<String,
	 * Object> resumen =
	 * syncService.sincronizarTodosLosDepartamentosAlt(soloModificados); return
	 * ResponseEntity.ok(resumen); }
	 * 
	 * @PostMapping("/centrodecosto/{codCentrodecosto}") public
	 * ResponseEntity<String> syncCentrodecosto(@PathVariable String
	 * codCentrodecosto) { return
	 * ResponseEntity.ok(syncService.sincronizarCentrodecosto(codCentrodecosto,
	 * true)); }
	 * 
	 * @PostMapping("/centrosdecosto/masivo") public ResponseEntity<Map<String,
	 * Object>> syncCentrosdecostoMasivo(
	 * 
	 * @RequestParam(defaultValue = "true") boolean soloModificados) { Map<String,
	 * Object> resumen =
	 * syncService.sincronizarTodosLosCentrosdecosto(soloModificados); return
	 * ResponseEntity.ok(resumen); }
	 */
}