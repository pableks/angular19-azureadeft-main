package com.example.Backend.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Config.RabbitMQConfig;
import com.example.Backend.Model.SignosVitales;
import com.example.Backend.Repository.SignosVitalesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class SignosVitalesProducer {

    private final RabbitTemplate rabbitTemplate;
    private final SignosVitalesRepository signosVitalesRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public SignosVitalesProducer(RabbitTemplate rabbitTemplate, 
                                SignosVitalesRepository signosVitalesRepository,
                                ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.signosVitalesRepository = signosVitalesRepository;
        this.objectMapper = objectMapper;
    }

    public void procesarSignosVitales(SignosVitales signos) {
        // Guardar en base de datos
        signos.setFechaRegistro(LocalDateTime.now());
        
        // Verificar si hay alertas
        List<String> alertas = new ArrayList<>();
        
        // Frecuencia cardíaca (normal: 60-100)
        if (signos.getFrecuenciaCardiaca() != null) {
            if (signos.getFrecuenciaCardiaca() < 60 || signos.getFrecuenciaCardiaca() > 100) {
                alertas.add("Frecuencia cardíaca anormal: " + signos.getFrecuenciaCardiaca());
            }
        }
        
        // Temperatura (normal: 36.1-37.2)
        if (signos.getTemperatura() != null) {
            if (signos.getTemperatura() < 36.1 || signos.getTemperatura() > 37.2) {
                alertas.add("Temperatura anormal: " + signos.getTemperatura());
            }
        }
        
        // Saturación de oxígeno (normal: >95%)
        if (signos.getSaturacionOxigeno() != null) {
            if (signos.getSaturacionOxigeno() < 95) {
                alertas.add("Saturación de oxígeno baja: " + signos.getSaturacionOxigeno() + "%");
            }
        }
        
        // Presión arterial (ejemplo: "120/80")
        if (signos.getPresionArterial() != null && !signos.getPresionArterial().isEmpty()) {
            String[] valores = signos.getPresionArterial().split("/");
            if (valores.length == 2) {
                int sistolica = Integer.parseInt(valores[0]);
                int diastolica = Integer.parseInt(valores[1]);
                
                if (sistolica > 140 || sistolica < 90 || diastolica > 90 || diastolica < 60) {
                    alertas.add("Presión arterial anormal: " + signos.getPresionArterial());
                }
            }
        }

        // Marcar si hay alertas
        signos.setEsAlerta(!alertas.isEmpty());
        signosVitalesRepository.save(signos);

        // Si hay alertas, enviar mensaje a RabbitMQ
        if (!alertas.isEmpty()) {
            ObjectNode alertaJson = objectMapper.createObjectNode()
                .put("pacienteId", signos.getPacienteId())
                .put("fecha", signos.getFechaRegistro().toString());
            
            alertaJson.putArray("alertas")
                .addAll(alertas.stream()
                    .map(alerta -> objectMapper.createObjectNode().put("mensaje", alerta))
                    .toList());

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_ALERTAS,
                alertaJson.toString()
            );
        }
    }
}
