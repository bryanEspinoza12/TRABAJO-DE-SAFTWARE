package com.sagit.incidentes.infrastructure.adapter.out.persistence;

import com.sagit.incidentes.domain.model.Usuario;
import com.sagit.incidentes.domain.port.out.UsuarioRepositoryPort;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.mapper.UsuarioMapper;
import com.sagit.incidentes.infrastructure.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de salida (Driven Adapter) para la persistencia de usuarios.
 * Conecta el puerto de salida con Spring Data JPA.
 */
@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;
    private final UsuarioMapper mapper;

    public UsuarioPersistenceAdapter(SpringDataUsuarioRepository repository, UsuarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        UsuarioEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
