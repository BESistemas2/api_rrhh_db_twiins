package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fabribat.apiNomina.entities.rrhh.BkpCargo;

@Repository
public interface BkpCargoRepository extends JpaRepository<BkpCargo, Long> {

    /**
     * Consulta incremental para obtener las novedades de cargos superiores a un ID de cambio.
     * @param cambCodigo Último código de auditoría procesado
     * @return Lista ordenada de eventos de auditoría
     */
    List<BkpCargo> findByCambCodigoGreaterThanOrderByCambCodigoAsc(Long cambCodigo);
}