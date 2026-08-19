package com.fabribat.apiNomina.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabribat.apiNomina.services.SincronizacionService;

@RestController
@RequestMapping("/api/v1")
public class SsoColaboradorController {

    @Autowired
    private SincronizacionService syncService;

    // Endpoint 1: Obtener la lista completa de empleados formateada para ORPHEUS
    @GetMapping("/sso-colaboradores")
    public ResponseEntity<List<Map<String, Object>>> getTodosLosColaboradores() {
        List<Map<String, Object>> response = syncService.obtenerPayloadTodosLosEmpleados();
        return ResponseEntity.ok(response);
    }

    // Endpoint 2: Obtener la información de un solo empleado por su cédula
    @GetMapping("/sso-colaborador/{cedula}")
    public ResponseEntity<Map<String, Object>> getColaboradorPorCedula(@PathVariable String cedula) {
        Map<String, Object> response = syncService.obtenerPayloadEmpleado(cedula);
        
        if (response.containsKey("error")) {
            return ResponseEntity.status(404).body(response); // Not Found si no existe
        }
        
        return ResponseEntity.ok(response);
    }
}