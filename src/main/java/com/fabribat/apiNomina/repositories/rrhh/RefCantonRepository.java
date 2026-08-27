package com.fabribat.apiNomina.repositories.rrhh;

import com.fabribat.apiNomina.entities.rrhh.RefCanton;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefCantonRepository extends JpaRepository<RefCanton, Short> {
}