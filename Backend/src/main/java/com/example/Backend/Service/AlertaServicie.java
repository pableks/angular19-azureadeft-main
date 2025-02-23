package com.example.Backend.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.Backend.Config.RabbitMQConfig;
import com.example.Backend.Model.Alerta;
import com.example.Backend.Model.SignosVitales;
import com.example.Backend.Repository.SignosVitalesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlertaServicie implements AlertaService {

    private final RabbitTemplate rabbitTemplate;
    private final SignosVitalesRepository signosVitalesRepository;
    private final ObjectMapper objectMapper;

    public AlertaServicie(RabbitTemplate rabbitTemplate, SignosVitalesRepository signosVitalesRepository, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.signosVitalesRepository = signosVitalesRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void procesarAlertas(List<Alerta> alertas) {
        try {
            String alertaMensaje = objectMapper.writeValueAsString(alertas);
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_ALERTAS,
                alertaMensaje
            );
            System.out.println("Alertas procesadas y enviadas a RabbitMQ: " + alertaMensaje);
        } catch (Exception e) {
            System.err.println("Error procesando alertas: " + e.getMessage());
        }
    }

    @Override
    public List<Alerta> obtenerAlertas(String pacienteId, String tipo) {
        // Implementar lógica para obtener alertas de la base de datos
        // Esto es un placeholder, la implementación real dependerá del repositorio
        return new ArrayList<>();
    }

    public void procesarSignosVitales(SignosVitales signosVitales) {
        signosVitales.setFechaRegistro(LocalDateTime.now());
        
        // Verificar si los signos vitales están fuera de rango
        List<String> alertas = new ArrayList<>();
        
        if (signosVitales.getFrecuenciaCardiaca() != null) {
            if (signosVitales.getFrecuenciaCardiaca() < 60 || signosVitales.getFrecuenciaCardiaca() > 100) {
                alertas.add("Frecuencia cardíaca anormal: " + signosVitales.getFrecuenciaCardiaca());
            }
        }
        
        if (signosVitales.getTemperatura() != null) {
            if (signosVitales.getTemperatura() < 36.0 || signosVitales.getTemperatura() > 38.0) {
                alertas.add("Temperatura anormal: " + signosVitales.getTemperatura());
            }
        }
        
        if (signosVitales.getSaturacionOxigeno() != null) {
            if (signosVitales.getSaturacionOxigeno() < 95) {
                alertas.add("Saturación de oxígeno baja: " + signosVitales.getSaturacionOxigeno());
            }
        }

        signosVitales.setEsAlerta(!alertas.isEmpty());
        signosVitalesRepository.save(signosVitales);

        if (!alertas.isEmpty()) {
            enviarAlerta(signosVitales, alertas);
        }
    }

    private void enviarAlerta(SignosVitales signosVitales, List<String> alertas) {
        try {
            String alertaMensaje = objectMapper.writeValueAsString(new AlertaMensaje(
                signosVitales.getPacienteId(),
                signosVitales.getFechaRegistro(),
                alertas
            ));
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_ALERTAS,
                alertaMensaje
            );
            
            System.out.println("Alerta enviada a RabbitMQ: " + alertaMensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar alerta: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public void generarResumenPeriodico() {
        try {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime hace5Minutos = ahora.minusMinutes(5);
            
            List<SignosVitales> registrosRecientes = signosVitalesRepository
                .findByPacienteIdAndFechaRegistroBetween(null, hace5Minutos, ahora);

            if (!registrosRecientes.isEmpty()) {
                String resumenMensaje = objectMapper.writeValueAsString(new ResumenPeriodico(
                    ahora,
                    registrosRecientes.size(),
                    registrosRecientes.stream().filter(SignosVitales::getEsAlerta).count()
                ));

                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_RESUMEN,
                    resumenMensaje
                );
                
                System.out.println("Resumen periódico enviado a RabbitMQ: " + resumenMensaje);
            }
        } catch (Exception e) {
            System.err.println("Error al generar resumen periódico: " + e.getMessage());
        }
    }

    // Clases internas para los mensajes
    private static class AlertaMensaje {
        private Long pacienteId;
        private LocalDateTime fecha;
        private List<String> alertas;

        public AlertaMensaje(Long pacienteId, LocalDateTime fecha, List<String> alertas) {
            this.pacienteId = pacienteId;
            this.fecha = fecha;
            this.alertas = alertas;
        }

        // Getters
        public Long getPacienteId() { return pacienteId; }
        public LocalDateTime getFecha() { return fecha; }
        public List<String> getAlertas() { return alertas; }
    }

    private static class ResumenPeriodico {
        private LocalDateTime fecha;
        private int totalRegistros;
        private long totalAlertas;

        public ResumenPeriodico(LocalDateTime fecha, int totalRegistros, long totalAlertas) {
            this.fecha = fecha;
            this.totalRegistros = totalRegistros;
            this.totalAlertas = totalAlertas;
        }

        // Getters
        public LocalDateTime getFecha() { return fecha; }
        public int getTotalRegistros() { return totalRegistros; }
        public long getTotalAlertas() { return totalAlertas; }
    }
}
