package com.fabribat.apiNomina.repositories.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fabribat.apiNomina.entities.security.RefApi;

@Repository
public interface RefApiRepository extends JpaRepository<RefApi, UUID> {
    Optional<RefApi> findByNomApi(String nomApi);
}