package com.example.Backend.DTO;


import java.time.LocalDateTime;

public class SignosVitalesDTO {

    private Long pacienteId;
    private Integer frecuenciaCardiaca;
    private String presionArterial;
    private Double temperatura;
    private Integer saturacionOxigeno;
    private LocalDateTime fechaRegistro;
    private Boolean esAlerta;

    public SignosVitalesDTO(Long pacienteId, Integer frecuenciaCardiaca, String presionArterial, Double temperatura,
        Integer saturacionOxigeno, LocalDateTime fechaRegistro, Boolean esAlerta) {
        this.pacienteId = pacienteId;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.presionArterial = presionArterial;
        this.temperatura = temperatura;
        this.saturacionOxigeno = saturacionOxigeno;
        this.fechaRegistro = fechaRegistro;
        this.esAlerta = esAlerta;
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
