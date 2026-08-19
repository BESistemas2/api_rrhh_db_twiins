package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import com.fabribat.apiNomina.entities.rrhh.RefDepartamento;

public interface RefDepartamentoRepository extends Repository<RefDepartamento, String> {
    List<RefDepartamento> findAll();
    Optional<RefDepartamento> findById(String id);
}