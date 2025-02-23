package com.example.Backend.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.Backend.Config.RabbitMQConfig;
import com.example.Backend.Model.Alerta;
import com.example.Backend.Model.Paciente;
import com.example.Backend.Repository.AlertaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.Backend.Service.WebSocketService;
import com.example.Backend.Service.PacienteService;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class AlertaConsumer {

    private final AlertaRepository alertaRepository;
    private final ObjectMapper objectMapper;
    private final WebSocketService webSocketService;
    private final PacienteService pacienteService;
    
    @Value("${alertas.archivos.ruta:/tmp/alertas}")
    private String rutaArchivos;

    public AlertaConsumer(
        AlertaRepository alertaRepository, 
        ObjectMapper objectMapper,
        WebSocketService webSocketService,
        PacienteService pacienteService
    ) {
        this.alertaRepository = alertaRepository;
        this.objectMapper = objectMapper;
        this.webSocketService = webSocketService;
        this.pacienteService = pacienteService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ALERTAS)
    public void recibirAlerta(String mensaje) {
        try {
            List<Alerta> alertas = objectMapper.readValue(mensaje, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Alerta.class));
            
            for (Alerta alerta : alertas) {
                // Look up patient information
                Optional<Paciente> paciente = pacienteService.getPacienteById(alerta.getPacienteId());
                
                if (paciente.isPresent()) {
                    // Create enriched alert with patient name
                    ObjectNode enrichedAlert = objectMapper.createObjectNode()
                        .put("id", alerta.getId())
                        .put("pacienteId", alerta.getPacienteId())
                        .put("pacienteNombre", paciente.get().getNombre() + " " + paciente.get().getApellido())
                        .put("fecha", alerta.getFecha().toString())
                        .put("descripcion", alerta.getDescripcion())
                        .put("tipo", alerta.getTipo());

                    // Save the original alert
                    Alerta alertaGuardada = alertaRepository.save(alerta);
                    
                    // Send enriched alert through WebSocket
                    webSocketService.enviarAlerta(alertaGuardada, enrichedAlert.toString());
                    System.out.println("Alerta procesada y guardada: " + enrichedAlert.toString());
                } else {
                    System.err.println("Paciente no encontrado para ID: " + alerta.getPacienteId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar alerta: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESUMEN)
    public void recibirResumen(String mensaje) {
        try {
            JsonNode resumenJson = objectMapper.readTree(mensaje);
            
            // Crear y guardar el resumen en la base de datos
            Alerta resumen = new Alerta();
            resumen.setFecha(LocalDateTime.parse(resumenJson.get("fecha").asText()));
            resumen.setDescripcion(String.format(
                "Resumen periódico - Total registros: %d, Total alertas: %d",
                resumenJson.get("totalRegistros").asInt(),
                resumenJson.get("totalAlertas").asLong()
            ));
            resumen.setTipo("RESUMEN");
            
            alertaRepository.save(resumen);
            
            // Generar archivo JSON
            generarArchivoJson(resumen);
            
            System.out.println("Resumen procesado y guardado: " + mensaje);
        } catch (Exception e) {
            System.err.println("Error al procesar resumen: " + e.getMessage());
        }
    }

    private void generarArchivoJson(Alerta alerta) {
        try {
            // Crear directorio si no existe
            File directorio = new File(rutaArchivos);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Generar nombre de archivo con timestamp
            String timestamp = alerta.getFecha().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = String.format("%s_%s_%d.json",
                alerta.getTipo().toLowerCase(),
                timestamp,
                alerta.getId()
            );

            // Escribir archivo JSON
            String rutaCompleta = Paths.get(rutaArchivos, nombreArchivo).toString();
            String jsonContent = objectMapper.writeValueAsString(alerta);
            Files.write(Paths.get(rutaCompleta), jsonContent.getBytes());

            System.out.println("Archivo JSON generado: " + rutaCompleta);
        } catch (Exception e) {
            System.err.println("Error al generar archivo JSON: " + e.getMessage());
        }
    }
}
