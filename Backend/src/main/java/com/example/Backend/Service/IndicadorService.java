package com.example.Backend.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Backend.Model.SignosVitales;

@Service
public class IndicadorService {
   private final AlertaServicie alertaServicie;

   public IndicadorService(AlertaServicie alertaServicie) {
       this.alertaServicie = alertaServicie;
   }

   public List<String> getAlertasMedicas(Long pacienteId, double presionSistolica, 
           double presionDiastolica, double oxigeno, double frecuenciaCardiaca, 
           double temperatura) {
       
       List<String> alertas = new ArrayList<>();

       // Convert double values to appropriate types
       Integer frecuenciaCardiacaInt = (int) Math.round(frecuenciaCardiaca);
       Integer saturacionOxigenoInt = (int) Math.round(oxigeno);
       String presionArterial = presionSistolica + "/" + presionDiastolica;

       // Presión arterial
       if (presionSistolica < 90 || presionDiastolica < 60) {
           alertas.add("Hipotensión detectada (PA: " + presionArterial + ")");
       } else if (presionSistolica > 140 || presionDiastolica > 90) {
           alertas.add("Hipertensión detectada (PA: " + presionArterial + ")");
       }

       // Saturación de oxígeno
       if (saturacionOxigenoInt < 90) {
           alertas.add("Niveles de oxígeno críticos (" + saturacionOxigenoInt + "%)");
       } else if (saturacionOxigenoInt < 95) {
           alertas.add("Niveles de oxígeno bajos (" + saturacionOxigenoInt + "%)");
       }

       // Frecuencia cardíaca
       if (frecuenciaCardiacaInt < 60) {
           alertas.add("Bradicardia detectada (" + frecuenciaCardiacaInt + " lpm)");
       } else if (frecuenciaCardiacaInt > 100) {
           alertas.add("Taquicardia detectada (" + frecuenciaCardiacaInt + " lpm)");
       }

       // Temperatura
       if (temperatura > 38.0) {
           alertas.add("Fiebre detectada (" + temperatura + "°C)");
       } else if (temperatura < 36.0) {
           alertas.add("Hipotermia detectada (" + temperatura + "°C)");
       }

       return alertas;
   }

   public String evaluarNivelRiesgo(List<String> alertas) {
       if (alertas.isEmpty()) {
           return "NORMAL";
       }

       int alertasCriticas = (int) alertas.stream()
           .filter(alerta -> 
               alerta.contains("críticos") || 
               alerta.contains("Hipotensión") ||
               alerta.contains("Hipertensión") ||
               alerta.contains("Bradicardia") ||
               alerta.contains("Taquicardia") ||
               alerta.contains("Hipotermia"))
           .count();

       if (alertasCriticas > 0) {
           return "ALTO";
       } else if (alertas.size() > 2) {
           return "MEDIO";
       } else {
           return "BAJO";
       }
   }

   public String generarResumenAlertas(List<String> alertas) {
       if (alertas.isEmpty()) {
           return "No se detectaron anomalías en los signos vitales.";
       }

       StringBuilder resumen = new StringBuilder();
       resumen.append("Se detectaron las siguientes anomalías:\n");
       alertas.forEach(alerta -> resumen.append("- ").append(alerta).append("\n"));
       resumen.append("\nNivel de riesgo: ").append(evaluarNivelRiesgo(alertas));

       return resumen.toString();
   }
}