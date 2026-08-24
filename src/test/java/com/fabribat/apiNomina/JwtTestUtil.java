package com.fabribat.apiNomina;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utilidad para generar tokens JWT válidos en tests de integración.
 * Usa la misma clave secreta que JwtUtil (inyectada desde application-test.properties).
 */
@Component
public class JwtTestUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final String EMISOR = "ApiNomina-BateriasEcuador";

    /**
     * Genera un token JWT válido para un usuario con rol API_CLIENT.
     * Útil para tests que requieren autenticación.
     */
    public String generateValidToken(String username) {
        return generateToken(username, "API_CLIENT", System.currentTimeMillis() + 3600000);
    }

    /**
     * Genera un token JWT con rol personalizado.
     */
    public String generateTokenWithRole(String username, String role) {
        return generateToken(username, role, System.currentTimeMillis() + 3600000);
    }

    /**
     * Genera un token JWT expirado (para tests de error 401).
     */
    public String generateExpiredToken(String username) {
        long now = System.currentTimeMillis();
        return generateToken(username, "API_CLIENT", now - 3600000); // Expirado hace 1 hora
    }

    private String generateToken(String username, String role, long expirationTime) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.create()
                .withIssuer(EMISOR)
                .withSubject(username)
                .withClaim("rol", role)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(expirationTime))
                .sign(algorithm);
    }
}