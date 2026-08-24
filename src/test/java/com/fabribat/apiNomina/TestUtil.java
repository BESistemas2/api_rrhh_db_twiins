package com.fabribat.apiNomina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utilidades comunes para tests de integración.
 */
public final class TestUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private TestUtil() {
    }

    /**
     * Convierte un objeto a JSON string.
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando a JSON", e);
        }
    }

    /**
     * Convierte JSON string a objeto del tipo especificado.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializando JSON", e);
        }
    }
}