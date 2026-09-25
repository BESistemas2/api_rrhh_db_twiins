package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.rrhh.NomiRefCentrodecosto;

public interface NomiRefCentrodecostoRepository extends CrudRepository<NomiRefCentrodecosto, Short> {
    List<NomiRefCentrodecosto> findAll();
    List<NomiRefCentrodecosto> findByEstCentrodecosto(String estCentrodecosto);
    List<NomiRefCentrodecosto> findByEstCentrodecostoIn(List<String> estados);
}