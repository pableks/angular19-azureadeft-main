package com.example.Kafka_consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.example.Kafka_consumer.model.Alerta;
import com.example.Kafka_consumer.model.SignosVitales;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AlertaService alertaService;
    private final SimpMessagingTemplate messagingTemplate;

    public KafkaConsumerService(
            KafkaTemplate<String, String> kafkaTemplate, 
            ObjectMapper objectMapper,
            AlertaService alertaService,
            SimpMessagingTemplate messagingTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.alertaService = alertaService;
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "signosvitales_topic", groupId = "group_idx")
    public void consumeSignosVitales(String message) {
        try {
            SignosVitales signosVitales = objectMapper.readValue(message, SignosVitales.class);
            
            // Convert numeric blood pressure to string format if needed
            if (signosVitales.getPresionArterial() == null || signosVitales.getPresionArterial().equals("0")) {
                signosVitales.setPresionArterial("0/0");
            }
            
            // Add debug logging
            logger.debug("Sending vital signs to WebSocket: {}", signosVitales);
            messagingTemplate.convertAndSend("/topic/signos-vitales", signosVitales);
            logger.debug("Vital signs sent to WebSocket successfully");
            
            List<String> anomalias = detectarAnomalias(signosVitales);
            
            if (!anomalias.isEmpty()) {
                Alerta alerta = new Alerta();
                alerta.setPacienteId(Long.parseLong(signosVitales.getPacienteId()));
                alerta.setFecha(LocalDateTime.now());
                alerta.setDescripcion(String.join(", ", anomalias));
                alerta.setTipo("ANOMALIA");
                
                String alertaJson = objectMapper.writeValueAsString(alerta);
                kafkaTemplate.send("alertas_topic", alertaJson);
                alertaService.guardarAlerta(alerta);
                
                // Send alert to WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/alertas", alerta);
            }
        } catch (Exception e) {
            logger.error("Error procesando mensaje: " + e.getMessage(), e);
        }
    }

    private List<String> detectarAnomalias(SignosVitales signos) {
        List<String> anomalias = new ArrayList<>();
        
        // Verificar frecuencia cardíaca (60-100 normal)
        if (signos.getFrecuenciaCardiaca() < 60 || signos.getFrecuenciaCardiaca() > 100) {
            anomalias.add("Frecuencia cardíaca anormal: " + signos.getFrecuenciaCardiaca());
        }
        
        // Verificar presión arterial (90-140/60-90 normal)
        try {
            String[] presion = signos.getPresionArterial().split("/");
            if (presion.length == 2) {
                double sistolica = Double.parseDouble(presion[0]);
                double diastolica = Double.parseDouble(presion[1]);
                
                if (sistolica < 90 || sistolica > 140 || diastolica < 60 || diastolica > 90) {
                    anomalias.add("Presión arterial anormal: " + signos.getPresionArterial());
                }
            } else {
                logger.warn("Invalid blood pressure format: {}", signos.getPresionArterial());
            }
        } catch (Exception e) {
            logger.warn("Error parsing blood pressure value: {}", signos.getPresionArterial());
        }
        
        // Verificar temperatura (36.5-37.5 normal)
        if (signos.getTemperatura() < 36.5 || signos.getTemperatura() > 37.5) {
            anomalias.add("Temperatura anormal: " + signos.getTemperatura());
        }
        
        // Verificar saturación de oxígeno (95-100 normal)
        if (signos.getSaturacionOxigeno() < 95) {
            anomalias.add("Saturación de oxígeno baja: " + signos.getSaturacionOxigeno());
        }
        
        return anomalias;
    }
}
