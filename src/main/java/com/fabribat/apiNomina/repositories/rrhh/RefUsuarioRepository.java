package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import com.fabribat.apiNomina.entities.rrhh.RefUsuario;

public interface RefUsuarioRepository extends Repository<RefUsuario, Long> {

    // Devuelve SOLO los empleados que estén Activos ('A')
    List<RefUsuario> findByEstUsuario(String estUsuario);

    // Busca a un empleado específico por cédula pero asegurando que esté Activo ('A')
    Optional<RefUsuario> findFirstByCedUsuarioAndEstUsuario(String cedUsuario, String estUsuario);
}