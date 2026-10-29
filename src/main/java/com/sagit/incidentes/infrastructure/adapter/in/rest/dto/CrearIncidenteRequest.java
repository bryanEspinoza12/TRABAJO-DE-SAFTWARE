package com.sagit.incidentes.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearIncidenteRequest {

    @NotBlank(message = "La fuente de monitoreo es obligatoria (ej. Prometheus, Grafana, Manual)")
    private String fuenteMonitoreo;

    @NotBlank(message = "El servicio afectado es obligatorio (ej. Servidor de Base de Datos)")
    private String servicioAfectado;

    @NotBlank(message = "La descripción de la falla es obligatoria (ej. Caída de servicio de caja)")
    private String descripcionFalla;

    /**
     * Prioridad del incidente: ALTA, MEDIA, BAJA. Es opcional en el formulario.
     */
    private String prioridad;

    public String getFuenteMonitoreo() {
        return fuenteMonitoreo;
    }

    public void setFuenteMonitoreo(String fuenteMonitoreo) {
        this.fuenteMonitoreo = fuenteMonitoreo;
    }

    public String getServicioAfectado() {
        return servicioAfectado;
    }

    public void setServicioAfectado(String servicioAfectado) {
        this.servicioAfectado = servicioAfectado;
    }

    public String getDescripcionFalla() {
        return descripcionFalla;
    }

    public void setDescripcionFalla(String descripcionFalla) {
        this.descripcionFalla = descripcionFalla;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
