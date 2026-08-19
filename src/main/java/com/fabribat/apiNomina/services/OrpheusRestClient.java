package com.fabribat.apiNomina.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OrpheusRestClient {

    private final RestTemplate restTemplate;
    
    @Value("${orpheus.api.base-url}")
    private String baseUrl;

    @Value("${orpheus.api.entidad}")
    private String entidadId;

    public OrpheusRestClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Método genérico para enviar peticiones POST a la API de ORPHEUS.
     * 
     * @param endpoint El sufijo de la URL (ej. "/set_sucursal")
     * @param payload El mapa de datos a enviar en formato JSON
     * @return La respuesta de la API en formato String (esperamos "TRUE")
     */
    private String sendPostRequest(String endpoint, Map<String, Object> payload) {
        String url = baseUrl + endpoint;

        // Inyectamos SIEMPRE el parámetro fijo "entidad" que exige ORPHEUS
        payload.put("entidad", entidadId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            // Aquí puedes agregar un log de error más elaborado
            System.err.println("❌ Error al consumir ORPHEUS en " + url + ": " + e.getMessage());
            throw new RuntimeException("Fallo la comunicación con ORPHEUS", e);
        }
    }

    // =========================================================================
    // MÉTODOS ESPECÍFICOS PARA CADA ENDPOINT DE ORPHEUS
    // =========================================================================

    public String setSucursal(Map<String, Object> sucursalData) {
        return sendPostRequest("/set_sucursal", sucursalData);
    }

    public String setDepartamento(Map<String, Object> departamentoData) {
        return sendPostRequest("/set_departamento", departamentoData);
    }

    public String setCargo(Map<String, Object> cargoData) {
        return sendPostRequest("/set_cargo", cargoData);
    }

    public String setEmpleado(Map<String, Object> empleadoData) {
        return sendPostRequest("/set_empleado", empleadoData);
    }
}