package com.sagit.incidentes.infrastructure.adapter.in.rest;

import com.sagit.incidentes.domain.model.Incidente;
import com.sagit.incidentes.domain.port.in.ConsultarIncidentesUseCase;
import com.sagit.incidentes.domain.port.in.CrearIncidenteUseCase;
import com.sagit.incidentes.infrastructure.adapter.in.rest.dto.CrearIncidenteRequest;
import com.sagit.incidentes.infrastructure.adapter.in.rest.dto.IncidenteResponse;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.mapper.IncidenteMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el módulo de Registro y Consulta de Incidentes TI.
 * Rutas protegidas bajo /api/v1/incidentes/**
 */
@RestController
@RequestMapping("/api/v1/incidentes")
public class IncidenteController {

    private final CrearIncidenteUseCase crearIncidenteUseCase;
    private final ConsultarIncidentesUseCase consultarIncidentesUseCase;
    private final IncidenteMapper incidenteMapper;

    public IncidenteController(CrearIncidenteUseCase crearIncidenteUseCase,
                               ConsultarIncidentesUseCase consultarIncidentesUseCase,
                               IncidenteMapper incidenteMapper) {
        this.crearIncidenteUseCase = crearIncidenteUseCase;
        this.consultarIncidentesUseCase = consultarIncidentesUseCase;
        this.incidenteMapper = incidenteMapper;
    }

    /**
     * Endpoint protegido para registrar un incidente TI.
     * POST /api/v1/incidentes
     * Extrae automáticamente el usuario que registra desde el Token JWT autenticado.
     */
    @PostMapping
    public ResponseEntity<IncidenteResponse> registrarIncidente(
            @Valid @RequestBody CrearIncidenteRequest request,
            Authentication authentication
    ) {
        String usuarioRegistro = authentication != null ? authentication.getName() : "SISTEMA";

        Incidente nuevoIncidente = incidenteMapper.toDomain(request, usuarioRegistro);
        Incidente creado = crearIncidenteUseCase.crear(nuevoIncidente);

        return ResponseEntity.status(HttpStatus.CREATED).body(incidenteMapper.toResponse(creado));
    }

    /**
     * Endpoint protegido para listar todos los incidentes ordenados por fecha de creación descendente.
     * GET /api/v1/incidentes
     */
    @GetMapping
    public ResponseEntity<List<IncidenteResponse>> listarIncidentes() {
        List<IncidenteResponse> lista = consultarIncidentesUseCase.listarTodos()
                .stream()
                .map(incidenteMapper::toResponse)
                .toList();

        return ResponseEntity.ok(lista);
    }

    /**
     * Endpoint protegido para consultar un incidente por ID.
     * GET /api/v1/incidentes/{id}
     * Retorna 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IncidenteResponse> obtenerPorId(@PathVariable Long id) {
        Incidente incidente = consultarIncidentesUseCase.buscarPorId(id);
        return ResponseEntity.ok(incidenteMapper.toResponse(incidente));
    }
}
