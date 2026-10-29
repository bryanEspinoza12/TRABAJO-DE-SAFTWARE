package com.sagit.incidentes.domain.port.out;

import com.sagit.incidentes.domain.model.Usuario;

import java.util.Optional;

/**
 * Puerto de salida para la persistencia y consulta de usuarios.
 */
public interface UsuarioRepositoryPort {
    Optional<Usuario> findByUsername(String username);
    Usuario save(Usuario usuario);
    boolean existsByUsername(String username);
}
