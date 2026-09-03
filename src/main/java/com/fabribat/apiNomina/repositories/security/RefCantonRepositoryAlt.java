package com.fabribat.apiNomina.repositories.security;

import com.fabribat.apiNomina.entities.security.RefCantonAlt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefCantonRepositoryAlt extends JpaRepository<RefCantonAlt, Short> {
}