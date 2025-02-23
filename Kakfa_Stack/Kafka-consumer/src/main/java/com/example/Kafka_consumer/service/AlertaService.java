package com.example.Kafka_consumer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Kafka_consumer.model.Alerta;
import com.example.Kafka_consumer.repository.AlertaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AlertaService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertaService.class);
    private final AlertaRepository alertaRepository;
    private final ObjectMapper objectMapper;
    
    public AlertaService(AlertaRepository alertaRepository, ObjectMapper objectMapper) {
        this.alertaRepository = alertaRepository;
        this.objectMapper = objectMapper;
    }
    
    @Transactional
    public void guardarAlerta(Alerta alerta) {
        try {
            logger.info("Intentando guardar alerta: {}", alerta);
            Alerta alertaGuardada = alertaRepository.save(alerta);
            logger.info("Alerta guardada exitosamente con ID: {}", alertaGuardada.getId());
        } catch (Exception e) {
            logger.error("Error al guardar la alerta: ", e);
            throw e;
        }
    }
    
    @KafkaListener(topics = "alertas", groupId = "alert-service-group")
    public void procesarAlerta(String alertaJson) {
        try {
            logger.info("Recibido mensaje de alerta: {}", alertaJson);
            Alerta alerta = objectMapper.readValue(alertaJson, Alerta.class);
            guardarAlerta(alerta);
            logger.info("Alerta procesada y guardada exitosamente");
        } catch (Exception e) {
            logger.error("Error procesando alerta: ", e);
        }
    }
} 