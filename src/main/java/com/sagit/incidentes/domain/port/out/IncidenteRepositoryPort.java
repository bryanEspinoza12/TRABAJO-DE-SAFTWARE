package com.sagit.incidentes.domain.port.out;

import com.sagit.incidentes.domain.model.Incidente;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia y consulta de incidentes TI.
 */
public interface IncidenteRepositoryPort {
    Incidente save(Incidente incidente);
    List<Incidente> findAllOrderByFechaCreacionDesc();
    Optional<Incidente> findById(Long id);
}
