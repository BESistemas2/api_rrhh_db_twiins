package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.rrhh.RefProvincia;

public interface RefProvinciaRepository extends CrudRepository<RefProvincia, Long> {
    List<RefProvincia> findAll();
}