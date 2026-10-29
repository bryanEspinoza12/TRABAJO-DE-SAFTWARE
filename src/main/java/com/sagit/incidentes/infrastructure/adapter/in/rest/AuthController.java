package com.sagit.incidentes.infrastructure.adapter.in.rest;

import com.sagit.incidentes.domain.model.AuthResult;
import com.sagit.incidentes.domain.port.in.AuthUseCase;
import com.sagit.incidentes.infrastructure.adapter.in.rest.dto.JwtAuthResponse;
import com.sagit.incidentes.infrastructure.adapter.in.rest.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el módulo de autenticación (Inicio de Sesión).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    /**
     * Endpoint público para iniciar sesión y obtener el token JWT.
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult authResult = authUseCase.login(request.getUsername(), request.getPassword());

        JwtAuthResponse response = JwtAuthResponse.builder()
                .token(authResult.getToken())
                .type(authResult.getType())
                .username(authResult.getUsername())
                .rol(authResult.getRol())
                .build();

        return ResponseEntity.ok(response);
    }
}
