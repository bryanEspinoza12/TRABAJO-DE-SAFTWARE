package com.sagit.incidentes.application.service;

import com.sagit.incidentes.domain.exception.BusinessRuleException;
import com.sagit.incidentes.domain.exception.IncidenteNotFoundException;
import com.sagit.incidentes.domain.model.EstadoIncidente;
import com.sagit.incidentes.domain.model.Incidente;
import com.sagit.incidentes.domain.port.in.ConsultarIncidentesUseCase;
import com.sagit.incidentes.domain.port.in.CrearIncidenteUseCase;
import com.sagit.incidentes.domain.port.out.IncidenteRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación para la gestión y consulta de incidentes TI.
 */
@Service
public class IncidenteService implements CrearIncidenteUseCase, ConsultarIncidentesUseCase {

    private final IncidenteRepositoryPort incidenteRepositoryPort;

    public IncidenteService(IncidenteRepositoryPort incidenteRepositoryPort) {
        this.incidenteRepositoryPort = incidenteRepositoryPort;
    }

    @Override
    public Incidente crear(Incidente incidente) {
        if (incidente == null) {
            throw new BusinessRuleException("El incidente no puede ser nulo");
        }

        if (incidente.getFuenteMonitoreo() == null || incidente.getFuenteMonitoreo().trim().isEmpty()) {
            throw new BusinessRuleException("La fuente de monitoreo es obligatoria");
        }
        if (incidente.getServicioAfectado() == null || incidente.getServicioAfectado().trim().isEmpty()) {
            throw new BusinessRuleException("El servicio afectado es obligatorio");
        }
        if (incidente.getDescripcionFalla() == null || incidente.getDescripcionFalla().trim().isEmpty()) {
            throw new BusinessRuleException("La descripción de la falla es obligatoria");
        }
        if (incidente.getUsuarioRegistro() == null || incidente.getUsuarioRegistro().trim().isEmpty()) {
            throw new BusinessRuleException("El usuario de registro es obligatorio y debe provenir de la sesión autenticada");
        }

        // Estado por defecto: ABIERTO
        if (incidente.getEstado() == null) {
            incidente.setEstado(EstadoIncidente.ABIERTO);
        }

        // Fecha de creación asignada automáticamente
        if (incidente.getFechaCreacion() == null) {
            incidente.setFechaCreacion(LocalDateTime.now());
        }

        return incidenteRepositoryPort.save(incidente);
    }

    @Override
    public List<Incidente> listarTodos() {
        return incidenteRepositoryPort.findAllOrderByFechaCreacionDesc();
    }

    @Override
    public Incidente buscarPorId(Long id) {
        return incidenteRepositoryPort.findById(id)
                .orElseThrow(() -> new IncidenteNotFoundException("Incidente no encontrado con ID: " + id));
    }
}
