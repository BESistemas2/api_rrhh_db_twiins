package com.fabribat.apiNomina.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Ahora inyectamos la utilidad de tokens correctamente
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. SALIDA TEMPRANA (Tu excelente aporte de Clean Code)
        // Si no hay cabecera o no empieza con "Bearer ", lo dejamos pasar.
        // (Spring Security lo bloqueará más adelante si la ruta es privada)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraemos el token limpio
        String token = authHeader.substring(7);

        // 3. Validamos el token usando el componente inyectado
        DecodedJWT datosToken = jwtUtil.validarToken(token);

        // 4. Si es válido, extraemos los datos y registramos al usuario
        if (datosToken != null) {
            String username = datosToken.getSubject();
            String rolOriginal = datosToken.getClaim("rol").asString();

            // Armamos el rol que Spring espera (ej. ROLE_ADMIN)
            String rolSpring = "ROLE_" + rolOriginal;
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolSpring);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, 
                    null,
                    Collections.singleton(authority)
            );

            // Metemos al usuario en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. Continuamos con el flujo normal
        filterChain.doFilter(request, response);
    }
}