package com.fabribat.apiNomina.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fabribat.apiNomina.entities.security.UsuarioApi;
import com.fabribat.apiNomina.repositories.security.UsuarioApiRepository;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioApiRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${sm://API_RRHH_S_USR_ORPH_PASSWORD}")
    private String defaultPassword;

    @Value("${sm://API_RRHH_G_USR_ORPH}")
    private String defaultUsername;

    public DataInitializer(UsuarioApiRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Si la tabla api_usuarios está vacía, inserta el usuario base
        if (usuarioRepository.count() == 0) {
            UsuarioApi admin = new UsuarioApi(
                defaultUsername,
                passwordEncoder.encode(defaultPassword),
                "API_CLIENT"
            );
            usuarioRepository.save(admin);
            log.info("Usuario '{}' registrado correctamente en la BD de GCP.", defaultUsername);
        }
    }
}