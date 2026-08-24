package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;


import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.rrhh.RefDepartamento;

public interface RefDepartamentoRepository extends CrudRepository<RefDepartamento, Short> {
    List<RefDepartamento> findAll();
}