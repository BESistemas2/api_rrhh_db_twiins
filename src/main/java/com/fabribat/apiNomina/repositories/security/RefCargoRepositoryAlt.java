package com.fabribat.apiNomina.repositories.security;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.security.RefCargoAlt;

public interface RefCargoRepositoryAlt extends CrudRepository<RefCargoAlt, Short> {
    List<RefCargoAlt> findAll();

}