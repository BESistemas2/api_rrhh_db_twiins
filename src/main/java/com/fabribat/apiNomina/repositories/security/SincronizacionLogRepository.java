package com.fabribat.apiNomina.repositories.security;

import com.fabribat.apiNomina.entities.security.SincronizacionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SincronizacionLogRepository extends JpaRepository<SincronizacionLog, Long> {
    Optional<SincronizacionLog> findByTipoEntidadAndCodigoEntidad(String tipoEntidad, String codigoEntidad);
}