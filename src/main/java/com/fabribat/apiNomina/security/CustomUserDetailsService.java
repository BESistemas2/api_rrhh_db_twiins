package com.fabribat.apiNomina.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fabribat.apiNomina.entities.security.UsuarioApi;
import com.fabribat.apiNomina.repositories.security.UsuarioApiRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioApiRepository usuarioApiRepository;

    public CustomUserDetailsService(UsuarioApiRepository usuarioApiRepository) {
        this.usuarioApiRepository = usuarioApiRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Consulta la base de datos de GCP a través de la conexión Primary
        UsuarioApi usuario = usuarioApiRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("El usuario se encuentra inactivo: " + username);
        }

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }
}