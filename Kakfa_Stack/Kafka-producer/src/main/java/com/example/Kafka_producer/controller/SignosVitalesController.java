package com.example.Kafka_producer.controller;

import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Kafka_producer.config.KafkaProducerConfig;
import com.example.Kafka_producer.model.SignosVitales;
import com.example.Kafka_producer.service.SignosVitalesService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class SignosVitalesController {

    @Autowired
    private SignosVitalesService producerService;

    @PostMapping("/signosvitales")
    public ResponseEntity<String> produce(@RequestParam(defaultValue = KafkaProducerConfig.SECOND_TOPIC) String topicNames,
            @RequestBody SignosVitales signosvitales)
            throws InterruptedException, ExecutionException {
        String successMessage = null;
        try {
            producerService.sendMessage(topicNames, signosvitales);
            successMessage = String.format(
                    "Successfully information to the '%s' topic. Please check the consumer.", topicNames);
            return ResponseEntity.status(HttpStatus.OK).body(successMessage);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON: " + e.getMessage());
        }
    }
}
