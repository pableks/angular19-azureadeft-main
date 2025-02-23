package com.example.Backend.Model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "Paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Apellido")
    private String apellido;

    @Column(name = "Edad")
    private int edad;

    @Column(name = "Habitacion")
    private String habitacion;

    @Column(name = "FrecuenciaCardiaca")
    private double frecuenciaCardiaca;

    @Column(name = "Oxigeno")
    private double oxigeno;

    @Column(name = "PresionSistolica")
    private double presionSistolica;

    @Column(name = "PresionDiastolica")
    private double presionDiastolica;

    @Column(name = "PresionArterial")
    private double presionArterial;

    @Column(name = "Temperatura")
    private double temperatura;

    @Column(name = "Condicion")
    private String condicion;

    @Column(name = "glucosa")
    private double glucosa;

    @Column(name = "UltimoControl")
    private LocalDateTime ultimoControl;

    @Column(name = "Observaciones")
    private String observaciones;

    @ElementCollection
    @CollectionTable(name = "HistorialMedico", joinColumns = @JoinColumn(name = "rut"))
    @Column(name = "Detalle")
    private List<String> historialMedico;
    
    @Column(name = "UltimaActualizacion")
    private LocalDateTime ultimaActualizacion;

    @ElementCollection
    @CollectionTable(name = "HistorialMedico", joinColumns = @JoinColumn(name = "rut"))
    @Column(name = "HistorialPresion")
    private List<Double> historialPresion;

    @ElementCollection
    @CollectionTable(name = "HistorialMedico", joinColumns = @JoinColumn(name = "rut"))
    @Column(name = "HistorialFrecuencia")
    private List<Double> historialFrecuencia;

    @ElementCollection
    @CollectionTable(name = "HistorialMedico", joinColumns = @JoinColumn(name = "rut"))
    @Column(name = "EvolucionCondicion")
    private List<String> evolucionCondicion;

    @ElementCollection
    @CollectionTable(name = "HistorialMedico", joinColumns = @JoinColumn(name = "rut"))
    @Column(name = "Medicamento")
    private List<String> medicamentos;

    @ElementCollection
    @CollectionTable(name = "HistorialMedico", joinColumns = @JoinColumn(name = "rut"))
    @Column(name = "UltimosControles")
    private List<LocalDateTime> ultimosControles;


    public List<LocalDateTime> getUltimosControles() {
        return ultimosControles;
    }

    public void setUltimosControles(List<LocalDateTime> ultimosControles) {
        this.ultimosControles = ultimosControles;
    }

    public Paciente() {
    }

    public Paciente(String nombre, String apellido, int edad, String habitacion, double frecuenciaCardiaca,
            double oxigeno, double presionSistolica, double presionDiastolica, double presionArterial,
            double temperatura, String condicion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.habitacion = habitacion;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.oxigeno = oxigeno;
        this.presionSistolica = presionSistolica;
        this.presionDiastolica = presionDiastolica;
        this.presionArterial = presionArterial;
        this.temperatura = temperatura;
        this.condicion = condicion;
    }

    // Getters y Setters
    public String getrut() {
        return rut;
    }

    public void setrut(String rut) {
        if (!rut.matches("\\d{7,8}-[Kk0-9]")) {
            throw new IllegalArgumentException("Formato de RUT inválido.");
        }
        this.rut = rut;
    }

    public double getGlucosa() {
        return glucosa;
    }

    public void setGlucosa(double glucosa) {
        this.glucosa = glucosa;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(String habitacion) {
        this.habitacion = habitacion;
    }

    public double getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(double frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public double getOxigeno() {
        return oxigeno;
    }

    public void setOxigeno(double oxigeno) {
        this.oxigeno = oxigeno;
    }

    public double getPresionSistolica() {
        return presionSistolica;
    }

    public void setPresionSistolica(double presionSistolica) {
        this.presionSistolica = presionSistolica;
    }

    public double getPresionDiastolica() {
        return presionDiastolica;
    }

    public void setPresionDiastolica(double presionDiastolica) {
        this.presionDiastolica = presionDiastolica;
    }

    public double getPresionArterial() {
        return presionArterial;
    }

    public void setPresionArterial(double presionArterial) {
        this.presionArterial = presionArterial;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public LocalDateTime getUltimoControl() {
        return ultimoControl;
    }

    public void setUltimoControl(LocalDateTime ultimoControl) {
        this.ultimoControl = ultimoControl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<String> getHistorialMedico() {
        return historialMedico;
    }

    public void setHistorialMedico(List<String> historialMedico) {
        this.historialMedico = historialMedico;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public List<String> getEvolucionCondicion() {
        return evolucionCondicion;
    }

    public void setEvolucionCondicion(List<String> evolucionCondicion) {
        this.evolucionCondicion = evolucionCondicion;
    }

    public List<String> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<String> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public List<Double> getHistorialPresion() {
        return historialPresion;
    }

    public void setHistorialPresion(List<Double> historialPresion) {
        this.historialPresion = historialPresion;
    }

    public List<Double> getHistorialFrecuencia() {
        return historialFrecuencia;
    }

    public void setHistorialFrecuencia(List<Double> historialFrecuencia) {
        this.historialFrecuencia = historialFrecuencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
