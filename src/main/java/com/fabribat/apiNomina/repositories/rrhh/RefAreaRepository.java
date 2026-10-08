package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fabribat.apiNomina.entities.rrhh.RefArea;

@Repository
public interface RefAreaRepository extends JpaRepository<RefArea, Short> {
    List<RefArea> findByEstArea(String estArea);
}