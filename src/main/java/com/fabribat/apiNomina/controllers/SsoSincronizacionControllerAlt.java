package com.fabribat.apiNomina.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    // ENDPOINTS INDIVIDUALES (EXISTENTES)
    // =========================================================================

    @PostMapping("/sucursal-matriz")
    public ResponseEntity<String> syncSucursalMatriz() {
        return ResponseEntity.ok(syncService.sincronizarSucursalPorDefecto());
    }

    @PostMapping("/departamento/{codDepartamento}")
    public ResponseEntity<String> syncDepartamento(@PathVariable String codDepartamento) {
        return ResponseEntity.ok(syncService.sincronizarDepartamentoAlt(codDepartamento));
    }

    @PostMapping("/cargo/{codCargo}")
    public ResponseEntity<String> syncCargo(@PathVariable String codCargo) {
        return ResponseEntity.ok(syncService.sincronizarCargoAlt(codCargo));
    }

    @PostMapping("/empleado/{cedula}")
    public ResponseEntity<String> syncEmpleado(@PathVariable String cedula) {
        return ResponseEntity.ok(syncService.sincronizarEmpleado(cedula));
    }

    // =========================================================================
    // ENDPOINTS MASIVOS DE SINCRONIZACION (EXISTENTES)
    // =========================================================================

    @PostMapping("/masiva")
    public ResponseEntity<Map<String, Object>> syncMasivo(
            @RequestParam(defaultValue = "true") boolean soloModificados) {
        Map<String, Object> resumen = syncService.sincronizarTodoMasivo(soloModificados);
        return ResponseEntity.ok(resumen);
    }

    @PostMapping("/empleados/masivo")
    public ResponseEntity<Map<String, Object>> syncEmpleadosMasivo(
            @RequestParam(defaultValue = "true") boolean soloModificados) {
        Map<String, Object> resumen = syncService.sincronizarTodosLosEmpleados(soloModificados);
        return ResponseEntity.ok(resumen);
    }

    // =========================================================================
    // NUEVOS ENDPOINTS MASIVOS DE ELIMINACION VIA SOAP
    // =========================================================================

    /**
     * Elimina en ORPHEUS todos los departamentos con estado 'I' o 'X' en la BD.
     */
    @PostMapping("/departamentos/eliminar-inactivos")
    public ResponseEntity<Map<String, Object>> eliminarDepartamentosInactivos() {
        Map<String, Object> resumen = syncService.eliminarDepartamentosInactivosSoap();
        return ResponseEntity.ok(resumen);
    }

    /**
     * Elimina en ORPHEUS todos los cargos/puestos con estado 'I' o 'X' en la BD.
     */
    @PostMapping("/cargos/eliminar-inactivos")
    public ResponseEntity<Map<String, Object>> eliminarCargosInactivos() {
        Map<String, Object> resumen = syncService.eliminarCargosInactivosSoap();
        return ResponseEntity.ok(resumen);
    }

    /**
     * Ejecuta la purga masiva de departamentos y cargos inactivos en un solo proceso.
     */
    @PostMapping("/purgar-inactivos-masivo")
    public ResponseEntity<Map<String, Object>> purgarInactivosMasivo() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("departamentos", syncService.eliminarDepartamentosInactivosSoap());
        resultado.put("cargos", syncService.eliminarCargosInactivosSoap());
        return ResponseEntity.ok(resultado);
    }
}