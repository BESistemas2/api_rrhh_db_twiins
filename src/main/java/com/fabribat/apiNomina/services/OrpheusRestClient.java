package com.fabribat.apiNomina.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrpheusRestClient {

    private static final Logger log = LoggerFactory.getLogger(OrpheusRestClient.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "https://www.bateriasecuador.orpheus2.com.ec";

    public String sendPostRequest(String endpoint, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + endpoint, request, String.class);

            return response.getBody();

        } catch (HttpStatusCodeException e) {
            log.error("❌ Error HTTP al consumir ORPHEUS en {}: {} {}", 
                    endpoint, e.getStatusCode(), e.getStatusText());
            return "ERROR: " + e.getStatusCode().value() + " " + e.getStatusText();

        } catch (Exception e) {
            log.error("❌ Error de conexión al consumir ORPHEUS en {}: {}", endpoint, e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    public String setSucursal(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", "47");
        return sendPostRequest("/sso/rest/set_sucursal", body);
    }

    public String setDepartamento(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", "47");
        return sendPostRequest("/sso/rest/set_departamento", body);
    }

    public String setCargo(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", "47");
        return sendPostRequest("/sso/rest/set_cargo", body);
    }

    public String setEmpleado(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", "47");
        return sendPostRequest("/sso/rest/set_empleado", body);
    }
}