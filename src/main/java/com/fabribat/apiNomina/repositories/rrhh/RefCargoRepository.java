package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.rrhh.RefCargo;

public interface RefCargoRepository extends CrudRepository<RefCargo, Short> {
    List<RefCargo> findAll();

}