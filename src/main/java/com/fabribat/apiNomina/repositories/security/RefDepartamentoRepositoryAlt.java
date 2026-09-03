package com.fabribat.apiNomina.repositories.security;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.security.RefDepartamentoAlt;

public interface RefDepartamentoRepositoryAlt extends CrudRepository<RefDepartamentoAlt, Short> {
    List<RefDepartamentoAlt> findAll();
}