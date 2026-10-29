package com.sagit.incidentes.application.service;

import com.sagit.incidentes.domain.exception.InvalidCredentialsException;
import com.sagit.incidentes.domain.model.AuthResult;
import com.sagit.incidentes.domain.model.Usuario;
import com.sagit.incidentes.domain.port.in.AuthUseCase;
import com.sagit.incidentes.domain.port.out.UsuarioRepositoryPort;
import com.sagit.incidentes.infrastructure.config.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación para la autenticación de usuarios y generación de tokens JWT.
 */
@Service
public class AuthService implements AuthUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UsuarioRepositoryPort usuarioRepositoryPort,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResult login(String username, String password) {
        Usuario usuario = usuarioRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas: usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new InvalidCredentialsException("La cuenta de usuario está desactivada");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new InvalidCredentialsException("Credenciales incorrectas: contraseña inválida");
        }

        String rolName = usuario.getRol() != null ? usuario.getRol().name() : "ANALISTA";
        String token = jwtTokenProvider.generateToken(usuario.getUsername(), rolName);

        return AuthResult.builder()
                .token(token)
                .type("Bearer")
                .username(usuario.getUsername())
                .rol(rolName)
                .build();
    }
}
