package com.fabribat.apiNomina.repositories.rrhh;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository; // 🛡️ Evita exponer save() o delete()
import com.fabribat.apiNomina.entities.rrhh.BkpUsuario;

public interface BkpUsuarioRepository extends Repository<BkpUsuario, Long> {
    
    // Magia de Spring Data: Busca por cédula, los ordena por fecha descendente y toma el primero (el más reciente)
    Optional<BkpUsuario> findFirstByCedUsuarioOrderByCambFechaDesc(String cedUsuario);
    // Trae solo los registros estrictamente nuevos
    List<BkpUsuario> findByCambCodigoGreaterThanOrderByCambCodigoAsc(Long ultimoCodigoProcesado);

}