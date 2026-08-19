package com.fabribat.apiNomina.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fabribat.apiNomina.entities.security.UsuarioApi;
import com.fabribat.apiNomina.repositories.security.UsuarioApiRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioApiRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioApiRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Si la tabla api_usuarios está vacía, inserta el usuario base
        if (usuarioRepository.count() == 0) {
            UsuarioApi admin = new UsuarioApi(
                "orpheus_admin",
                passwordEncoder.encode("Orpheus2026!"),
                "API_CLIENT"
            );
            usuarioRepository.save(admin);
            System.out.println(">>> Usuario 'orpheus_admin' registrado correctamente en la BD de GCP.");
        }
    }
}