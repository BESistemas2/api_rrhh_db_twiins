package com.fabribat.apiNomina.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fabribat.apiNomina.services.SincronizacionService;

@RestController
@RequestMapping("/api/v1/sincronizacion")
public class SincronizacionController {

    @Autowired
    private SincronizacionService syncService;

    // =========================================================================
    // ENDPOINTS INDIVIDUALES (EXISTENTES - SIN CAMBIOS)
    // =========================================================================

    @PostMapping("/sucursal-matriz")
    public ResponseEntity<String> syncSucursalMatriz() {
        return ResponseEntity.ok(syncService.sincronizarSucursalPorDefecto());
    }

    @PostMapping("/departamento/{codDepartamento}")
    public ResponseEntity<String> syncDepartamento(@PathVariable String codDepartamento) {
        return ResponseEntity.ok(syncService.sincronizarDepartamento(codDepartamento));
    }

    @PostMapping("/cargo/{codCargo}")
    public ResponseEntity<String> syncCargo(@PathVariable String codCargo) {
        return ResponseEntity.ok(syncService.sincronizarCargo(codCargo));
    }

    @PostMapping("/empleado/{cedula}")
    public ResponseEntity<String> syncEmpleado(@PathVariable String cedula) {
        return ResponseEntity.ok(syncService.sincronizarEmpleado(cedula));
    }

    // =========================================================================
    // NUEVOS ENDPOINTS MASIVOS (OPCIONALES)
    // =========================================================================

    /**
     * Sincroniza masivamente toda la empresa (Sucursal, Departamentos, Cargos y Empleados).
     * @param soloModificados (Opcional, default: true). 
     *                        Si es true, solo envía registros nuevos o modificados (MD5).
     *                        Si es false, fuerza el reenvío de todo.
     */
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
}