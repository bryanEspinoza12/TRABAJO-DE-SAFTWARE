package com.sagit.incidentes.application.service;

import com.sagit.incidentes.domain.exception.InvalidCredentialsException;
import com.sagit.incidentes.domain.model.AuthResult;
import com.sagit.incidentes.domain.model.Rol;
import com.sagit.incidentes.domain.model.Usuario;
import com.sagit.incidentes.domain.port.out.UsuarioRepositoryPort;
import com.sagit.incidentes.infrastructure.config.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .id(1L)
                .username("admin")
                .password("$2a$10$hashedPassword")
                .nombreCompleto("Administrador")
                .rol(Rol.ADMIN)
                .activo(true)
                .build();
    }

    @Test
    void login_ConCredencialesCorrectas_DebeRetornarAuthResult() {
        when(usuarioRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("admin123", usuarioValido.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken("admin", "ADMIN")).thenReturn("fake-jwt-token");

        AuthResult resultado = authService.login("admin", "admin123");

        assertNotNull(resultado);
        assertEquals("fake-jwt-token", resultado.getToken());
        assertEquals("Bearer", resultado.getType());
        assertEquals("admin", resultado.getUsername());
        assertEquals("ADMIN", resultado.getRol());
    }

    @Test
    void login_ConUsuarioNoExistente_DebeLanzarInvalidCredentialsException() {
        when(usuarioRepositoryPort.findByUsername("no_existe")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () ->
                authService.login("no_existe", "admin123")
        );
    }

    @Test
    void login_ConPasswordIncorrecta_DebeLanzarInvalidCredentialsException() {
        when(usuarioRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("erroneo", usuarioValido.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () ->
                authService.login("admin", "erroneo")
        );
    }

    @Test
    void login_ConUsuarioInactivo_DebeLanzarInvalidCredentialsException() {
        usuarioValido.setActivo(false);
        when(usuarioRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(usuarioValido));

        assertThrows(InvalidCredentialsException.class, () ->
                authService.login("admin", "admin123")
        );
    }
}
