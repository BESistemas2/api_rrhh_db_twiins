package com.fabribat.apiNomina.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrpheusRestClient {

    private static final Logger log = LoggerFactory.getLogger(OrpheusRestClient.class);
    private final RestTemplate restTemplate;

    public OrpheusRestClient() {
        // Usa Apache HttpClient 5 para la conexión HTTP/TLS en lugar del motor nativo de Java.
        // Esto cambia la huella (fingerprint) de la petición para evitar el bloqueo 403 del WAF.
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Value("${orpheus.api.base-url:https://www.bateriasecuador.orpheus2.com.ec/sso/rest}")
    private String baseUrl;

    @Value("${orpheus.api.entidad:114}")
    private String entidad;

    public String sendPostRequest(String endpoint, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            // Imita el conjunto completo de cabeceras de un navegador/Bruno
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            headers.set("Accept-Language", "es-ES,es;q=0.9,en;q=0.8");
            headers.set("Connection", "keep-alive");

            // Convertimos todos los valores del payload original a String
            Map<String, String> stringPayload = new HashMap<>();
            if (payload != null) {
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    stringPayload.put(entry.getKey(), entry.getValue() != null ? String.valueOf(entry.getValue()) : null);
                }
            }

            // Imprimir el JSON real (con comillas) para verificar en consola
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonReal = mapper.writeValueAsString(stringPayload);
                System.out.println("Headers: " + headers);
                System.out.println("JSON REAL a enviar a " + endpoint + ": " + jsonReal);
            } catch (Exception e) {
                System.out.println("Error imprimiendo JSON: " + e.getMessage());
            }

            // Enviamos el Map<String, String>
            HttpEntity<Map<String, String>> request = new HttpEntity<>(stringPayload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + endpoint, request, String.class);

            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("❌ Error HTTP al consumir ORPHEUS en {}{}: {} {}", 
                    baseUrl, endpoint, e.getStatusCode(), e.getStatusText());
            return "ERROR: " + e.getStatusCode().value() + " " + e.getStatusText();
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