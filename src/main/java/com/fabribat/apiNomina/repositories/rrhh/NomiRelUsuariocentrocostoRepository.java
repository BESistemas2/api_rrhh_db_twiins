package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fabribat.apiNomina.entities.rrhh.NomiRelUsuariocentrocosto;
import com.fabribat.apiNomina.entities.rrhh.NomiRelUsuariocentrocostoPK;

public interface NomiRelUsuariocentrocostoRepository extends CrudRepository<NomiRelUsuariocentrocosto, NomiRelUsuariocentrocostoPK> {
    
    List<NomiRelUsuariocentrocosto> findAll();
    
    // Obtiene toda la entidad filtrada por usuario
    List<NomiRelUsuariocentrocosto> findByUsrUsuario(String usrUsuario);
    
    // Aprovecha el índice "idx_nomi_uc_usuario_porcentaje" para traerlos ordenados de mayor a menor porcentaje
    List<NomiRelUsuariocentrocosto> findByUsrUsuarioOrderByPorCentrocostoDesc(String usrUsuario);
    
    // Retorna solo la lista de IDs (cod_centrocosto) de ese usuario ordenados por el porcentaje mayor
    @Query("SELECT r.codCentrocosto FROM NomiRelUsuariocentrocosto r WHERE r.usrUsuario = :usrUsuario ORDER BY r.porCentrocosto DESC")
    List<Integer> findCodCentrocostoByUsrUsuarioOrderByPorcentaje(@Param("usrUsuario") String usrUsuario);
    
    Optional<NomiRelUsuariocentrocosto> findFirstByUsrUsuario(String usrUsuario);
}