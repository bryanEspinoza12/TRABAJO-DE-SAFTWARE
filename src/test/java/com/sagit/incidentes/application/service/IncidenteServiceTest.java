package com.sagit.incidentes.application.service;

import com.sagit.incidentes.domain.exception.BusinessRuleException;
import com.sagit.incidentes.domain.exception.IncidenteNotFoundException;
import com.sagit.incidentes.domain.model.EstadoIncidente;
import com.sagit.incidentes.domain.model.Incidente;
import com.sagit.incidentes.domain.port.out.IncidenteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidenteServiceTest {

    @Mock
    private IncidenteRepositoryPort incidenteRepositoryPort;

    @InjectMocks
    private IncidenteService incidenteService;

    private Incidente incidenteValido;

    @BeforeEach
    void setUp() {
        incidenteValido = Incidente.builder()
                .id(1L)
                .fuenteMonitoreo("Prometheus")
                .servicioAfectado("Servidor de Base de Datos")
                .descripcionFalla("Caída de servicio de caja")
                .prioridad("ALTA")
                .usuarioRegistro("admin")
                .estado(EstadoIncidente.ABIERTO)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void crear_ConDatosValidos_DebeGuardarYRetornarIncidente() {
        when(incidenteRepositoryPort.save(any(Incidente.class))).thenAnswer(invocation -> {
            Incidente arg = invocation.getArgument(0);
            arg.setId(10L);
            return arg;
        });

        Incidente aCrear = Incidente.builder()
                .fuenteMonitoreo("Grafana")
                .servicioAfectado("API de Pagos")
                .descripcionFalla("Timeout en transacciones")
                .prioridad("MEDIA")
                .usuarioRegistro("admin")
                .build();

        Incidente creado = incidenteService.crear(aCrear);

        assertNotNull(creado);
        assertEquals(10L, creado.getId());
        assertEquals(EstadoIncidente.ABIERTO, creado.getEstado());
        assertNotNull(creado.getFechaCreacion());
        verify(incidenteRepositoryPort, times(1)).save(any(Incidente.class));
    }

    @Test
    void crear_SinFuenteMonitoreo_DebeLanzarBusinessRuleException() {
        incidenteValido.setFuenteMonitoreo("");

        assertThrows(BusinessRuleException.class, () ->
                incidenteService.crear(incidenteValido)
        );
    }

    @Test
    void crear_SinServicioAfectado_DebeLanzarBusinessRuleException() {
        incidenteValido.setServicioAfectado(null);

        assertThrows(BusinessRuleException.class, () ->
                incidenteService.crear(incidenteValido)
        );
    }

    @Test
    void listarTodos_DebeRetornarListaDeIncidentes() {
        when(incidenteRepositoryPort.findAllOrderByFechaCreacionDesc()).thenReturn(List.of(incidenteValido));

        List<Incidente> lista = incidenteService.listarTodos();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Prometheus", lista.get(0).getFuenteMonitoreo());
    }

    @Test
    void buscarPorId_Existente_DebeRetornarIncidente() {
        when(incidenteRepositoryPort.findById(1L)).thenReturn(Optional.of(incidenteValido));

        Incidente encontrado = incidenteService.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        assertEquals("Servidor de Base de Datos", encontrado.getServicioAfectado());
    }

    @Test
    void buscarPorId_Inexistente_DebeLanzarIncidenteNotFoundException() {
        when(incidenteRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IncidenteNotFoundException.class, () ->
                incidenteService.buscarPorId(99L)
        );
    }
}
