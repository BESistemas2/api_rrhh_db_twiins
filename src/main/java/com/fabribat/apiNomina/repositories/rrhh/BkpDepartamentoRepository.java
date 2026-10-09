package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fabribat.apiNomina.entities.rrhh.BkpDepartamento;

@Repository
public interface BkpDepartamentoRepository extends JpaRepository<BkpDepartamento, Long> {

    /**
     * Consulta incremental para obtener las novedades de departamentos superiores a un ID de cambio.
     * @param cambCodigo Último código de auditoría procesado
     * @return Lista ordenada de eventos de auditoría
     */
    List<BkpDepartamento> findByCambCodigoGreaterThanOrderByCambCodigoAsc(Long cambCodigo);
}