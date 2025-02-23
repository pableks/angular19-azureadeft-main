package com.example.Backend.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PacienteDTO {
    
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private int edad;
    private String habitacion;
    private double frecuenciaCardiaca;
    private double oxigeno;
    private double presionSistolica;
    private double presionDiastolica;
    private double presionArterial;
    private double temperatura;
    private String condicion;
    private LocalDateTime ultimoControl;
    private String observaciones;
    private List<String> historialMedico;
    private List<String> alergias;
    private List<String> medicamentos;
    private double imc;
    private LocalDateTime ultimaRevision;
    private double glucosa;


   

   

    public PacienteDTO(Long id, String rut, String nombre, String apellido, int edad, String habitacion, double frecuenciaCardiaca, 
            double oxigeno, double presionSistolica, double presionDiastolica, double presionArterial, 
            double temperatura, String condicion, LocalDateTime ultimoControl, String observaciones, 
            List<String> historialMedico, List<String> alergias, List<String> medicamentos, 
            double imc, LocalDateTime ultimaRevision, double glucosa) {
        this.id = id;
        this.rut = rut;
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
        this.ultimoControl = ultimoControl;
        this.observaciones = observaciones;
        this.historialMedico = historialMedico;
        this.alergias = alergias;
        this.medicamentos = medicamentos;
        this.imc = imc;
        this.ultimaRevision = ultimaRevision;
        this.glucosa = glucosa;
    }

    // Métodos de cálculo de indicadores
    public String getEstadoPresion() {
        if (presionSistolica < 90 || presionDiastolica < 60) {
            return "Hipotensión";
        } else if (presionSistolica > 140 || presionDiastolica > 90) {
            return "Hipertensión";
        }
        return "Normal";
    }

    public String getEstadoOxigeno() {
        if (oxigeno < 90) return "Crítico";
        if (oxigeno < 95) return "Bajo";
        return "Normal";
    }

    public String evaluarAlertaFrecuencia(double frecuencia) {
        if (frecuencia < 60) return "Bradicardia";
        if (frecuencia > 100) return "Taquicardia";
        return "Normal";
    }

    public String evaluarAlertaTemperatura(double temperatura) {
        if (temperatura < 35) return "Hipotermia";
        if (temperatura > 37.5 && temperatura <= 38) return "Febrícula";
        if (temperatura > 38) return "Fiebre";
        return "Normal";
    }

    public String getEstadoGlucosa() {
        if (glucosa < 70) return "Hipoglucemia";
        if (glucosa > 180) return "Hiperglucemia";
        return "Normal";
    }

    // Nuevos métodos para reportes de salud
    public String getRiesgoCardiovascular() {
        int score = 0;
        
        // Edad
        if (edad > 45) score += 1;
        
        // Presión arterial
        if (getEstadoPresion().equals("Hipertensión")) score += 2;
        
        // IMC
        if (imc > 30) score += 1;
        
        // Glucosa
        if (getEstadoGlucosa().equals("Hiperglucemia")) score += 1;
        
        if (score >= 4) return "Alto";
        if (score >= 2) return "Moderado";
        return "Bajo";
    }

    public String generarResumenSalud() {
        return String.format("""
            Resumen de Salud:
            - Estado General: %s
            - Riesgo Cardiovascular: %s
            - IMC: %.1f (%s)
            - Últimos Valores:
              * Presión: %.0f/%.0f mmHg (%s)
              * Oxígeno: %.1f%% (%s)
              * Glucosa: %.1f mg/dL (%s)
              * Temperatura: %.1f°C
            """,
            condicion,
            getRiesgoCardiovascular(),
            imc, getEstadoIMC(),
            presionSistolica, presionDiastolica, getEstadoPresion(),
            oxigeno, getEstadoOxigeno(),
            glucosa, getEstadoGlucosa(),
            temperatura
        );
    }

    public List<String> getAlertasMedicas() {
        List<String> alertas = new ArrayList<>();
        
        if (getEstadoPresion().equals("Hipertensión")) {
            alertas.add("Presión arterial elevada");
        }
        if (getEstadoOxigeno().equals("Crítico")) {
            alertas.add("Niveles de oxígeno críticos");
        }
        if (getEstadoGlucosa().equals("Hiperglucemia")) {
            alertas.add("Niveles de glucosa elevados");
        }
        if (temperatura > 38) {
            alertas.add("Fiebre presente");
        }
        
        return alertas;
    }

    public String getEstadoIMC() {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25) return "Normal";
        if (imc < 30) return "Sobrepeso";
        return "Obesidad";
    }

    public double calcularEdadBiologica() {
        double edadBio = edad;
        
        // Factores que aumentan edad biológica
        if (getRiesgoCardiovascular().equals("Alto")) edadBio += 5;
        if (imc > 30) edadBio += 3;
        if (getEstadoGlucosa().equals("Hiperglucemia")) edadBio += 2;
        
        // Factores que disminuyen edad biológica
        if (getEstadoPresion().equals("Normal")) edadBio -= 2;
        if (imc >= 18.5 && imc <= 25) edadBio -= 3;
        
        return Math.max(edad, edadBio); // No puede ser menor que la edad cronológica
    }

    public String generarRecomendaciones() {
        StringBuilder recomendaciones = new StringBuilder();
        
        if (getEstadoIMC().equals("Obesidad")) {
            recomendaciones.append("- Consultar con nutricionista\n");
            recomendaciones.append("- Incrementar actividad física\n");
        }
        
        if (getEstadoPresion().equals("Hipertensión")) {
            recomendaciones.append("- Reducir consumo de sal\n");
            recomendaciones.append("- Monitorear presión regularmente\n");
        }
        
        if (getEstadoGlucosa().equals("Hiperglucemia")) {
            recomendaciones.append("- Controlar ingesta de carbohidratos\n");
            recomendaciones.append("- Realizar exámenes de glucosa periódicos\n");
        }
        
        if (temperatura > 38) {
            recomendaciones.append("- Mantener hidratación\n");
            recomendaciones.append("- Consultar por posible infección\n");
        }
        
        return recomendaciones.length() > 0 ? recomendaciones.toString() : "No se requieren recomendaciones específicas";
    }

    // Getters y Setters (mantenidos igual)
    public String getrut() {
        return rut;
    }

    public void setIdPaciente(String rut) {
        if (!rut.matches("\\d{7,8}-[Kk0-9]")) {
            throw new IllegalArgumentException("Formato de RUT inválido.");
        }
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public List<String> getHistorialMedico() {
        return historialMedico;
    }

    public void setHistorialMedico(List<String> historialMedico) {
        this.historialMedico = historialMedico;
    }

    public List<String> getAlergias() {
        return alergias;
    }

    public void setAlergias(List<String> alergias) {
        this.alergias = alergias;
    }

    public List<String> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<String> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public double getImc() {
        return imc;
    }

    public void setImc(double imc) {
        this.imc = imc;
    }

    public LocalDateTime getUltimaRevision() {
        return ultimaRevision;
    }

    public void setUltimaRevision(LocalDateTime ultimaRevision) {
        this.ultimaRevision = ultimaRevision;
    }

    public double getGlucosa() {
        return glucosa;
    }

    public void setGlucosa(double glucosa) {
        this.glucosa = glucosa;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public double getPresionArterial() {
        return presionArterial;
    }

    public void setPresionArterial(double presionArterial) {
        this.presionArterial = presionArterial;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
