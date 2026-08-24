package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import com.fabribat.apiNomina.entities.rrhh.RefCargo;

public interface RefCargoRepository extends Repository<RefCargo, Short> {
    List<RefCargo> findAll();
    Optional<RefCargo> findById(String id);
}