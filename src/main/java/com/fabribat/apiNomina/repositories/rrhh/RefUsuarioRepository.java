package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import com.fabribat.apiNomina.entities.rrhh.RefUsuario;

public interface RefUsuarioRepository extends Repository<RefUsuario, String> {

    // Devuelve SOLO los empleados que estén Activos ('A')
    List<RefUsuario> findByEstUsuario(String estUsuario);

    // Busca a un empleado específico por cédula pero asegurando que esté Activo ('A')
    Optional<RefUsuario> findFirstByCedUsuarioAndEstUsuario(String cedUsuario, String estUsuario);
    
   // En RefUsuarioRepository.java
    List<RefUsuario> findByCodCargentiexteAndEstUsuario(Short codCargentiexte, String estUsuario);
    
 // 1. Áreas asociadas a colaboradores activos (a través de ref_departamento)
    @Query("SELECT DISTINCT d.codArea FROM RefUsuario u " +
           "JOIN RefDepartamento d ON u.codDepartamento = d.codDepartamento " +
           "WHERE u.estUsuario = 'A' AND u.codDepartamento IS NOT NULL AND d.codArea IS NOT NULL AND d.codArea <> 0")
    List<Short> obtenerCodigosAreasDeUsuariosActivos();

    // 2. Departamentos asociados a colaboradores activos
    @Query("SELECT DISTINCT u.codDepartamento FROM RefUsuario u " +
           "WHERE u.estUsuario = 'A' AND u.codDepartamento IS NOT NULL AND u.codDepartamento <> 0")
    List<Short> obtenerCodigosDepartamentosDeUsuariosActivos();

    // 3. Cargos asociados a colaboradores activos
    @Query("SELECT DISTINCT u.codCargentiexte FROM RefUsuario u " +
           "WHERE u.estUsuario = 'A' AND u.codCargentiexte IS NOT NULL AND u.codCargentiexte <> 0")
    List<Short> obtenerCodigosCargosDeUsuariosActivos();
}