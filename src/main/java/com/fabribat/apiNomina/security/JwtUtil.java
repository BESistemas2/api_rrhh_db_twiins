package com.fabribat.apiNomina.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component // <-- CLAVE: Le dice a Spring que administre esta clase
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${sm://API_RRHH_S_JWT_SECRET}")
    private String CLAVE_SECRETA;
    private final String EMISOR = "ApiNomina-BateriasEcuador";
    private final long TIEMPO_EXPIRACION = 3600000; // 1 hora

    public String generarToken(String username, String rol) {
        Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);
        long tiempoActual = System.currentTimeMillis();
        Date fechaExpiracion = new Date(tiempoActual + TIEMPO_EXPIRACION);

        return JWT.create()
                .withIssuer(EMISOR)
                .withSubject(username)
                .withClaim("rol", rol)
                .withIssuedAt(new Date(tiempoActual))
                .withExpiresAt(fechaExpiracion)
                .sign(algoritmo);
    }

    public DecodedJWT validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);
            JWTVerifier verificador = JWT.require(algoritmo)
                    .withIssuer(EMISOR)
                    .build();
            
            // Si el token es falso o expiró, esta línea lanza una excepción
            return verificador.verify(token); 
            
        } catch (JWTVerificationException e) {
            log.error("Error de validación del token: " + e.getMessage());
            return null; // Retornamos null para que Spring Security deniegue el acceso
        }
    }
}