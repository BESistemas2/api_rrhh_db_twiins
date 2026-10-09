package com.fabribat.apiNomina.repositories.security;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fabribat.apiNomina.entities.security.RefAreaAlt;

@Repository
public interface RefAreaRepositoryAlt extends JpaRepository<RefAreaAlt, Short> {
    List<RefAreaAlt> findByEstArea(String estArea);
}