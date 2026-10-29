package com.sagit.incidentes.domain.exception;

public class IncidenteNotFoundException extends RuntimeException {
    public IncidenteNotFoundException(String message) {
        super(message);
    }
}
