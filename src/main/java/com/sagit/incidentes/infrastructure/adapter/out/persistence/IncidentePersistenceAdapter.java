package com.sagit.incidentes.infrastructure.adapter.out.persistence;

import com.sagit.incidentes.domain.model.Incidente;
import com.sagit.incidentes.domain.port.out.IncidenteRepositoryPort;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.entity.IncidenteEntity;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.mapper.IncidenteMapper;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.repository.SpringDataIncidenteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida (Driven Adapter) para la persistencia de incidentes TI.
 * Conecta el puerto de salida del dominio con Spring Data JPA.
 */
@Component
public class IncidentePersistenceAdapter implements IncidenteRepositoryPort {

    private final SpringDataIncidenteRepository repository;
    private final IncidenteMapper mapper;

    public IncidentePersistenceAdapter(SpringDataIncidenteRepository repository, IncidenteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Incidente save(Incidente incidente) {
        IncidenteEntity entity = mapper.toEntity(incidente);
        IncidenteEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Incidente> findAllOrderByFechaCreacionDesc() {
        return repository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Incidente> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
