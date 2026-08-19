package com.fabribat.apiNomina.repositories.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fabribat.apiNomina.entities.security.UsuarioApi;

@Repository
public interface UsuarioApiRepository extends JpaRepository<UsuarioApi, UUID> {
    
    // Spring Boot escribirá la consulta SQL automáticamente gracias a este nombre de método
    Optional<UsuarioApi> findByUsername(String username);
    
}