package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import com.fabribat.apiNomina.entities.rrhh.RefProvincia;

public interface RefProvinciaRepository extends Repository<RefProvincia, Long> {
    List<RefProvincia> findAll();
    Optional<RefProvincia> findById(Long id);
}