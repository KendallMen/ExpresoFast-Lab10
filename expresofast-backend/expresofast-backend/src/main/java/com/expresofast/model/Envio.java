package com.expresofast.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigoRastreo;

    @Column(nullable = false, length = 100)
    private String destinatario;

    @Column(nullable = false, length = 200)
    private String direccionDestino;

    @Column(nullable = false)
    private Double montoFlete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public Envio() {
    }

    public Envio(String codigoRastreo, String destinatario, String direccionDestino,
                 Double montoFlete, EstadoEnvio estado) {
        this.codigoRastreo = codigoRastreo;
        this.destinatario = destinatario;
        this.direccionDestino = direccionDestino;
        this.montoFlete = montoFlete;
        this.estado = estado;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoRastreo() { return codigoRastreo; }
    public void setCodigoRastreo(String codigoRastreo) { this.codigoRastreo = codigoRastreo; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }
    public Double getMontoFlete() { return montoFlete; }
    public void setMontoFlete(Double montoFlete) { this.montoFlete = montoFlete; }
    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
