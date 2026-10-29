package com.sagit.incidentes.domain.port.in;

import com.sagit.incidentes.domain.model.Incidente;

/**
 * Puerto de entrada para el caso de uso de creación de incidentes.
 */
public interface CrearIncidenteUseCase {
    Incidente crear(Incidente incidente);
}
