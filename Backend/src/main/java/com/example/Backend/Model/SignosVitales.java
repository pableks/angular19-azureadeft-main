package com.example.Backend.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SIGNOS_VITALES")
public class SignosVitales {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "PACIENTE_ID", nullable = false)
    private Long pacienteId;
    
    @Column(name = "FRECUENCIA_CARDIACA")
    private Integer frecuenciaCardiaca;
    
    @Column(name = "PRESION_ARTERIAL")
    private String presionArterial;
    
    @Column(name = "TEMPERATURA")
    private Double temperatura;
    
    @Column(name = "SATURACION_OXIGENO")
    private Integer saturacionOxigeno;
    
    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "ES_ALERTA")
    private Boolean esAlerta;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public String getPresionArterial() {
        return presionArterial;
    }

    public void setPresionArterial(String presionArterial) {
        this.presionArterial = presionArterial;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getSaturacionOxigeno() {
        return saturacionOxigeno;
    }

    public void setSaturacionOxigeno(Integer saturacionOxigeno) {
        this.saturacionOxigeno = saturacionOxigeno;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getEsAlerta() {
        return esAlerta;
    }

    public void setEsAlerta(Boolean esAlerta) {
        this.esAlerta = esAlerta;
    }
}
