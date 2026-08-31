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
            return ResponseEntity.status(404).body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // PROVINCIAS
    // =========================================================================
    @GetMapping("/sso-provincias")
    public ResponseEntity<List<Map<String, Object>>> getTodasLasProvincias() {
        List<Map<String, Object>> response = syncService.obtenerTodasLasProvincias();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sso-provincia/{codigo}")
    public ResponseEntity<Map<String, Object>> getProvinciaPorCodigo(@PathVariable Long codigo) {
        Map<String, Object> response = syncService.obtenerProvinciaPorCodigo(codigo);
        
        if (response.containsKey("error")) {
            return ResponseEntity.status(404).body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // CIUDADES
    // =========================================================================
    @GetMapping("/sso-ciudades")
    public ResponseEntity<List<Map<String, Object>>> getTodasLasCiudades() {
        List<Map<String, Object>> response = syncService.obtenerTodasLasCiudades();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sso-ciudad/{codigo}")
    public ResponseEntity<Map<String, Object>> getCiudadPorCodigo(@PathVariable Long codigo) {
        Map<String, Object> response = syncService.obtenerCiudadPorCodigo(codigo);
        
        if (response.containsKey("error")) {
            return ResponseEntity.status(404).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // =========================================================================
    // CANTONES
    // =========================================================================
    @GetMapping("/sso-cantones")
    public ResponseEntity<List<Map<String, Object>>> getTodosLosCantones() {
        List<Map<String, Object>> response = syncService.obtenerTodosLosCantones();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sso-canton/{codigo}")
    public ResponseEntity<Map<String, Object>> getCantonPorCodigo(@PathVariable Short codigo) {
        Map<String, Object> response = syncService.obtenerCantonPorCodigo(codigo);
        
        if (response.containsKey("error")) {
            return ResponseEntity.status(404).body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // CARGOS
    // =========================================================================
    @GetMapping("/sso-cargos")
    public ResponseEntity<List<Map<String, Object>>> getTodosLosCargos() {
        List<Map<String, Object>> response = syncService.obtenerTodosLosCargos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sso-cargo/{codigo}")
    public ResponseEntity<Map<String, Object>> getCargoPorCodigo(@PathVariable Long codigo) {
        Map<String, Object> response = syncService.obtenerCargoPorCodigo(codigo);
        
        if (response.containsKey("error")) {
            return ResponseEntity.status(404).body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // DEPARTAMENTOS
    // =========================================================================
    @GetMapping("/sso-departamentos")
    public ResponseEntity<List<Map<String, Object>>> getTodosLosDepartamentos() {
        List<Map<String, Object>> response = syncService.obtenerTodosLosDepartamentos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sso-departamento/{codigo}")
    public ResponseEntity<Map<String, Object>> getDepartamentoPorCodigo(@PathVariable String codigo) {
        Map<String, Object> response = syncService.obtenerDepartamentoPorCodigo(codigo);
        
        if (response.containsKey("error")) {
            return ResponseEntity.status(404).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
}