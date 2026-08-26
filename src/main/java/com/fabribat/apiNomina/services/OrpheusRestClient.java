package com.fabribat.apiNomina.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrpheusRestClient {

    private static final Logger log = LoggerFactory.getLogger(OrpheusRestClient.class);

    // Cliente HTTP nativo de Java (sin dependencias extra ni RestTemplate)
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${orpheus.api.base-url:https://www.bateriasecuador.orpheus2.com.ec/sso/rest}")
    private String baseUrl;

    @Value("${orpheus.api.entidad:114}")
    private String entidad;

    public String sendPostRequest(String endpoint, Map<String, Object> payload) {
        try {
            // 1. Mantenemos el orden y forzamos a String
            Map<String, String> stringPayload = new LinkedHashMap<>();
            if (payload != null) {
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    stringPayload.put(entry.getKey(), entry.getValue() != null ? String.valueOf(entry.getValue()) : null);
                }
            }

            String jsonBody = objectMapper.writeValueAsString(stringPayload);
            System.out.println("JSON enviado a " + endpoint + ": " + jsonBody);

            // 2. Cabeceras idénticas a cURL / Bruno (ÚNICAMENTE Content-Type)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                log.error("❌ Error HTTP al consumir ORPHEUS en {}{}: {} {}", 
                        baseUrl, endpoint, response.statusCode(), response.body());
                return "ERROR: " + response.statusCode() + " " + response.body();
            }

            return response.body();

        } catch (Exception e) {
            log.error("❌ Error de conexión al consumir ORPHEUS en {}{}: {}", baseUrl, endpoint, e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    public String setSucursal(Map<String, Object> payload) {
        Map<String, Object> body = new LinkedHashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_sucursal", body);
    }

    public String setDepartamento(Map<String, Object> payload) {
        Map<String, Object> body = new LinkedHashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_departamento", body);
    }

    public String setCargo(Map<String, Object> payload) {
        Map<String, Object> body = new LinkedHashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_cargo", body);
    }

    public String setEmpleado(Map<String, Object> payload) {
        Map<String, Object> body = new LinkedHashMap<>(payload);
        body.put("entidad", entidad);
        return sendPostRequest("/set_empleado", body);
    }
}