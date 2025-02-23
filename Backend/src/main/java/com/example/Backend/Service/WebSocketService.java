package com.example.Backend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Backend.Model.Alerta;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public WebSocketService(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void enviarAlerta(Alerta alerta, String enrichedAlertJson) {
        try {
            // Send to general topic
            messagingTemplate.convertAndSend("/topic/alertas", enrichedAlertJson);
            System.out.println("WebSocket - Enviando alerta general: " + enrichedAlertJson);

            // Send to patient-specific topic if pacienteId exists
            if (alerta.getPacienteId() != null) {
                String destinoPaciente = String.format("/topic/alertas/%d", alerta.getPacienteId());
                messagingTemplate.convertAndSend(destinoPaciente, enrichedAlertJson);
                System.out.println("WebSocket - Enviando alerta a paciente: " + destinoPaciente);
            }
            
            logger.debug("Alerta enviada exitosamente por WebSocket");
        } catch (Exception e) {
            logger.error("Error al enviar alerta por WebSocket: " + e.getMessage(), e);
            System.err.println("WebSocket Error: " + e.getMessage());
        }
    }
} 