package com.sagit.incidentes.domain.port.in;

import com.sagit.incidentes.domain.model.AuthResult;

/**
 * Puerto de entrada para el caso de uso de autenticación.
 */
public interface AuthUseCase {
    AuthResult login(String username, String password);
}
