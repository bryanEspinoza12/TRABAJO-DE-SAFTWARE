package com.sagit.incidentes.infrastructure.adapter.out.persistence.mapper;

import com.sagit.incidentes.domain.model.Usuario;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

/**
 * Mapeador entre el modelo de dominio Usuario y la entidad JPA UsuarioEntity.
 */
@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return Usuario.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .nombreCompleto(entity.getNombreCompleto())
                .rol(entity.getRol())
                .activo(entity.getActivo())
                .build();
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) {
            return null;
        }
        return UsuarioEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .nombreCompleto(domain.getNombreCompleto())
                .rol(domain.getRol())
                .activo(domain.getActivo())
                .build();
    }
}
