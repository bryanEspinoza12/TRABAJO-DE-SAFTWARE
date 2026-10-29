package com.sagit.incidentes.infrastructure.adapter.out.persistence.mapper;

import com.sagit.incidentes.domain.model.EstadoIncidente;
import com.sagit.incidentes.domain.model.Incidente;
import com.sagit.incidentes.infrastructure.adapter.in.rest.dto.CrearIncidenteRequest;
import com.sagit.incidentes.infrastructure.adapter.in.rest.dto.IncidenteResponse;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.entity.IncidenteEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapeador entre modelo de dominio Incidente, entidad JPA IncidenteEntity y DTOs REST.
 */
@Component
public class IncidenteMapper {

    public Incidente toDomain(IncidenteEntity entity) {
        if (entity == null) {
            return null;
        }
        return Incidente.builder()
                .id(entity.getId())
                .fuenteMonitoreo(entity.getFuenteMonitoreo())
                .servicioAfectado(entity.getServicioAfectado())
                .descripcionFalla(entity.getDescripcionFalla())
                .estado(entity.getEstado())
                .prioridad(entity.getPrioridad())
                .usuarioRegistro(entity.getUsuarioRegistro())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    public IncidenteEntity toEntity(Incidente domain) {
        if (domain == null) {
            return null;
        }
        return IncidenteEntity.builder()
                .id(domain.getId())
                .fuenteMonitoreo(domain.getFuenteMonitoreo())
                .servicioAfectado(domain.getServicioAfectado())
                .descripcionFalla(domain.getDescripcionFalla())
                .estado(domain.getEstado())
                .prioridad(domain.getPrioridad())
                .usuarioRegistro(domain.getUsuarioRegistro())
                .fechaCreacion(domain.getFechaCreacion())
                .build();
    }

    public Incidente toDomain(CrearIncidenteRequest request, String usuarioRegistro) {
        if (request == null) {
            return null;
        }
        return Incidente.builder()
                .fuenteMonitoreo(request.getFuenteMonitoreo())
                .servicioAfectado(request.getServicioAfectado())
                .descripcionFalla(request.getDescripcionFalla())
                .prioridad(request.getPrioridad())
                .usuarioRegistro(usuarioRegistro)
                .estado(EstadoIncidente.ABIERTO)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    public IncidenteResponse toResponse(Incidente domain) {
        if (domain == null) {
            return null;
        }
        return IncidenteResponse.builder()
                .id(domain.getId())
                .fuenteMonitoreo(domain.getFuenteMonitoreo())
                .servicioAfectado(domain.getServicioAfectado())
                .descripcionFalla(domain.getDescripcionFalla())
                .estado(domain.getEstado() != null ? domain.getEstado().name() : null)
                .prioridad(domain.getPrioridad())
                .usuarioRegistro(domain.getUsuarioRegistro())
                .fechaCreacion(domain.getFechaCreacion())
                .build();
    }
}
