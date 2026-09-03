package com.fabribat.apiNomina.repositories.security;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.security.RefProvinciaAlt;

public interface RefProvinciaRepositoryAlt extends CrudRepository<RefProvinciaAlt, Long> {
    List<RefProvinciaAlt> findAll();
}