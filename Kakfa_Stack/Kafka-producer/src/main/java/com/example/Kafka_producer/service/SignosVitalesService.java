package com.example.Kafka_producer.service;

import com.example.Kafka_producer.model.SignosVitales;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Retryable;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class SignosVitalesService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    @Retryable(maxAttempts = 5)
    public CompletableFuture<SendResult<String, String>> sendMessage(String topicName, SignosVitales signosVitales) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(signosVitales);
        return this.kafkaTemplate.send(topicName, message);
    }

    @Value("${kafka.topic}")
    private String topicName;

    private static final String VITAL_SIGNS_ENDPOINT = "http://localhost:8085/api/signos-vitales";

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(SignosVitalesService.class);

    public SignosVitalesService() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::publishVitalSigns, 0, 5, TimeUnit.SECONDS);
    }

    private SignosVitales addFluctuations(SignosVitales baseSignos) {
        // Add small random fluctuations to each vital sign
        int heartRateVar = (int) (baseSignos.getFrecuenciaCardiaca() * (1 + random.nextGaussian() * 0.03)); // ±3% variation
        
        // Parse systolic and diastolic from the original pressure
        String[] pressure = baseSignos.getPresionArterial().split("/");
        int systolic = Integer.parseInt(pressure[0]);
        int diastolic = Integer.parseInt(pressure[1]);
        
        // Add percentage-based variations to blood pressure
        int systolicVar = (int) (systolic * (1 + random.nextGaussian() * 0.04)); // ±4% variation
        int diastolicVar = (int) (diastolic * (1 + random.nextGaussian() * 0.04)); // ±4% variation
        
        // Add percentage-based variations to temperature and SpO2
        double tempVar = baseSignos.getTemperatura() * (1 + random.nextGaussian() * 0.003); // ±0.3% variation
        int spo2Var = (int) (baseSignos.getSaturacionOxigeno() * (1 + random.nextGaussian() * 0.01)); // ±1% variation

        // Basic sanity checks to avoid physically impossible values
        heartRateVar = Math.max(20, heartRateVar);  // Prevent negative or zero values
        systolicVar = Math.max(40, systolicVar);
        diastolicVar = Math.max(20, diastolicVar);
        tempVar = Math.max(35.0, Math.min(42.0, tempVar));  // Normal range is ~36.1-37.2°C, allowing for fever up to 42°C
        spo2Var = Math.max(50, Math.min(100, spo2Var));  // SpO2 can't be over 100%

        return new SignosVitales(
            baseSignos.getPacienteId(),
            heartRateVar,
            String.format("%d/%d", systolicVar, diastolicVar),
            Math.round(tempVar * 10.0) / 10.0, // Round to 1 decimal place
            spo2Var
        );
    }

    private void publishVitalSigns() {
        try {
            String vitalSignsJson = restTemplate.getForObject(VITAL_SIGNS_ENDPOINT, String.class);
            JsonNode vitalSignsNode = objectMapper.readTree(vitalSignsJson);

            if (vitalSignsNode.isArray()) {
                for (JsonNode vitalSignNode : vitalSignsNode) {
                    String pacienteId = vitalSignNode.get("pacienteId").asText();
                    int frecuenciaCardiaca = vitalSignNode.get("frecuenciaCardiaca").asInt();
                    String presionArterial = vitalSignNode.get("presionArterial").asText();
                    double temperatura = vitalSignNode.get("temperatura").asDouble();
                    int saturacionOxigeno = vitalSignNode.get("saturacionOxigeno").asInt();

                    SignosVitales baseSignos = new SignosVitales(
                        pacienteId,
                        frecuenciaCardiaca,
                        presionArterial,
                        temperatura,
                        saturacionOxigeno
                    );

                    // Add fluctuations before sending
                    SignosVitales fluctuatingSignos = addFluctuations(baseSignos);
                    
                    sendMessage(topicName, fluctuatingSignos);
                    logger.info("Published fluctuating vital signs to Kafka topic {}: {}", topicName, fluctuatingSignos);
                }
            } else {
                logger.warn("El JSON recibido no es un arreglo. No se puede procesar.");
            }
        } catch (IOException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching vital signs: {}", e.getMessage());
        }
    }
}