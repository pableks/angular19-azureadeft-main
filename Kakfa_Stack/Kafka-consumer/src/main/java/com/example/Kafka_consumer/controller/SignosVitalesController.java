package com.example.Kafka_consumer.controller;

import com.example.Kafka_consumer.model.SignosVitales;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/signosvitales")
public class SignosVitalesController {

    private static final Logger logger = LoggerFactory.getLogger(SignosVitalesController.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC_NAME = "signosvitales_topic";

    public SignosVitalesController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> publishSignosVitales(@RequestBody SignosVitales signosVitales) {
        try {
            logger.info("Recibida petición para procesar signos vitales: {}", signosVitales);
            String message = objectMapper.writeValueAsString(signosVitales);
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Mensaje enviado exitosamente a Kafka");
                } else {
                    logger.error("Error al enviar mensaje a Kafka", ex);
                }
            });
            
            return ResponseEntity.ok("Mensaje enviado correctamente a Kafka");
        } catch (Exception e) {
            logger.error("Error al procesar la petición", e);
            return ResponseEntity.internalServerError()
                .body("Error al enviar el mensaje: " + e.getMessage());
        }
    }
} 