package com.example.Backend.Service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.Backend.Config.RabbitMQConfig;
import com.example.Backend.Model.SignosVitales;
import com.example.Backend.Repository.SignosVitalesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SignosVitalesResumenProducer {

    private final RabbitTemplate rabbitTemplate;
    private final SignosVitalesRepository signosVitalesRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public SignosVitalesResumenProducer(RabbitTemplate rabbitTemplate,
                                      SignosVitalesRepository signosVitalesRepository,
                                      ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.signosVitalesRepository = signosVitalesRepository;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 300000) // Ejecutar cada 5 minutos
    public void generarYEnviarResumen() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime haceCincoMinutos = ahora.minusMinutes(5);
        
        // Obtener signos vitales de los últimos 5 minutos
        List<SignosVitales> signosRecientes = signosVitalesRepository
            .findByFechaRegistroBetween(haceCincoMinutos, ahora);
            
        // Generar resumen estadístico
        ObjectNode resumenJson = objectMapper.createObjectNode()
            .put("fechaInicio", haceCincoMinutos.toString())
            .put("fechaFin", ahora.toString())
            .put("totalRegistros", signosRecientes.size());
            
        // Calcular promedios
        if (!signosRecientes.isEmpty()) {
            double promedioFrecuencia = signosRecientes.stream()
                .filter(s -> s.getFrecuenciaCardiaca() != null)
                .mapToInt(SignosVitales::getFrecuenciaCardiaca)
                .average()
                .orElse(0);
                
            double promedioTemperatura = signosRecientes.stream()
                .filter(s -> s.getTemperatura() != null)
                .mapToDouble(SignosVitales::getTemperatura)
                .average()
                .orElse(0);
                
            double promedioSaturacion = signosRecientes.stream()
                .filter(s -> s.getSaturacionOxigeno() != null)
                .mapToDouble(SignosVitales::getSaturacionOxigeno)
                .average()
                .orElse(0);
                
            resumenJson.put("promedioFrecuenciaCardiaca", promedioFrecuencia)
                       .put("promedioTemperatura", promedioTemperatura)
                       .put("promedioSaturacionOxigeno", promedioSaturacion);
        }
        
        // Enviar resumen a RabbitMQ
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY_RESUMEN,
            resumenJson.toString()
        );
    }
}
