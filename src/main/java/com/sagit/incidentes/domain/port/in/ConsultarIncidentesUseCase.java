package com.sagit.incidentes.domain.port.in;

import com.sagit.incidentes.domain.model.Incidente;

import java.util.List;

/**
 * Puerto de entrada para la consulta de incidentes.
 */
public interface ConsultarIncidentesUseCase {
    List<Incidente> listarTodos();
    Incidente buscarPorId(Long id);
}
