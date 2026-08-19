package com.fabribat.apiNomina.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabribat.apiNomina.services.SincronizacionService;

@RestController
@RequestMapping("/api/v1/sincronizacion")
public class SincronizacionController {

    @Autowired
    private SincronizacionService syncService;

    // 1. Sincronizar Sucursal por Defecto
    @PostMapping("/sucursal-matriz")
    public ResponseEntity<String> syncSucursalMatriz() {
        String respuesta = syncService.sincronizarSucursalPorDefecto();
        return ResponseEntity.ok(respuesta);
    }

    // 2. Sincronizar Departamento
    @PostMapping("/departamento/{codDepartamento}")
    public ResponseEntity<String> syncDepartamento(@PathVariable String codDepartamento) {
        String respuesta = syncService.sincronizarDepartamento(codDepartamento);
        return ResponseEntity.ok(respuesta);
    }

    // 3. Sincronizar Cargo
    @PostMapping("/cargo/{codCargo}")
    public ResponseEntity<String> syncCargo(@PathVariable String codCargo) {
        String respuesta = syncService.sincronizarCargo(codCargo);
        return ResponseEntity.ok(respuesta);
    }

    // 4. Sincronizar Empleado
    @PostMapping("/empleado/{cedula}")
    public ResponseEntity<String> syncEmpleado(@PathVariable String cedula) {
        String respuesta = syncService.sincronizarEmpleado(cedula);
        return ResponseEntity.ok(respuesta);
    }
}