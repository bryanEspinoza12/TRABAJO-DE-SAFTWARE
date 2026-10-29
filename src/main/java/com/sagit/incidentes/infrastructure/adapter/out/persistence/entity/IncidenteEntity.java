package com.sagit.incidentes.infrastructure.adapter.out.persistence.entity;

import com.sagit.incidentes.domain.model.EstadoIncidente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla 'incidentes' en SQL Server.
 */
@Entity
@Table(name = "incidentes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fuente_monitoreo", nullable = false, length = 100)
    private String fuenteMonitoreo;

    @Column(name = "servicio_afectado", nullable = false, length = 150)
    private String servicioAfectado;

    @Column(name = "descripcion_falla", nullable = false, length = 1000)
    private String descripcionFalla;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoIncidente estado;

    @Column(name = "prioridad", length = 20)
    private String prioridad;

    @Column(name = "usuario_registro", nullable = false, length = 50)
    private String usuarioRegistro;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public EstadoIncidente getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidente estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
