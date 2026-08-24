package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.fabribat.apiNomina.entities.rrhh.RefCiudad;

public interface RefCiudadRepository extends CrudRepository<RefCiudad, Long> {
    List<RefCiudad> findAll();
    
    // Útil para buscar ciudades por provincia
    // List<RefCiudad> findByProvinciaId(Long provinciaId); 
}