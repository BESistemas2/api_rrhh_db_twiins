package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import com.fabribat.apiNomina.entities.rrhh.RefCiudad;

public interface RefCiudadRepository extends Repository<RefCiudad, Long> {
    List<RefCiudad> findAll();
    Optional<RefCiudad> findById(Long id);
    
    // Útil para buscar ciudades por provincia
    // List<RefCiudad> findByProvinciaId(Long provinciaId); 
}