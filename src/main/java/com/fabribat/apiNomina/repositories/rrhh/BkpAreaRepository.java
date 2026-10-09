package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fabribat.apiNomina.entities.rrhh.BkpArea;

@Repository
public interface BkpAreaRepository extends JpaRepository<BkpArea, Long> {

    /**
     * Consulta incremental para obtener las novedades de áreas superiores a un ID de cambio.
     * @param cambCodigo Último código de auditoría procesado
     * @return Lista ordenada de eventos de auditoría
     */
    List<BkpArea> findByCambCodigoGreaterThanOrderByCambCodigoAsc(Long cambCodigo);
}