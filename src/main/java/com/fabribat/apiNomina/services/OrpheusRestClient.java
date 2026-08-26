package com.fabribat.apiNomina.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${orpheus.api.base-url:https://www.bateriasecuador.orpheus2.com.ec/sso/rest}")
    private String baseUrl;

    @Value("${orpheus.api.entidad:114}")
    private String entidad;

    public String sendPostRequest(String endpoint, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 1. Agregamos el Accept para evitar bloqueos de firewalls/WAF
            headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
            
            // Simulamos un User-Agent de navegador para evitar bloqueos de WAF
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            
            // 2. Convertimos todos los valores del payload original a String
            Map<String, String> stringPayload = new HashMap<>();
            if (payload != null) {
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    // String.valueOf() convierte números (ej. 1) a texto (ej. "1")
                    stringPayload.put(entry.getKey(), entry.getValue() != null ? String.valueOf(entry.getValue()) : null);
                }
            }

            System.out.println("Headers: " + headers);
            System.out.println("Payload a enviar: " + stringPayload); // En consola se verá igual, pero el JSON final enviará comillas.
            
            // 3. Enviamos el Map<String, String> para garantizar que el JSON lleve comillas en todo
            HttpEntity<Map<String, String>> request = new HttpEntity<>(stringPayload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + endpoint, request, String.class);

            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("❌ Error HTTP al consumir ORPHEUS en {}{}: {} {}", 
                    baseUrl, endpoint, e.getStatusCode(), e.getStatusText());
            return "ERROR: " + e.getStatusCode().value() + " " + e.getStatusText() + " " + payload;
        } catch (Exception e) {
            log.error("❌ Error de conexión al consumir ORPHEUS en {}{}: {}", baseUrl, endpoint, e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
    
    public String setSucursal(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_sucursal", body);
    }

    public String setDepartamento(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_departamento", body);
    }

    public String setCargo(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_cargo", body);
    }

    public String setEmpleado(Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_empleado", body);
    }
}