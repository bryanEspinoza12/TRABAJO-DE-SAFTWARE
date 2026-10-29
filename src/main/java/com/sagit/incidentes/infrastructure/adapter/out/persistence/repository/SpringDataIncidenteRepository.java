package com.sagit.incidentes.infrastructure.adapter.out.persistence.repository;

import com.sagit.incidentes.infrastructure.adapter.out.persistence.entity.IncidenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataIncidenteRepository extends JpaRepository<IncidenteEntity, Long> {
    List<IncidenteEntity> findAllByOrderByFechaCreacionDesc();
}
