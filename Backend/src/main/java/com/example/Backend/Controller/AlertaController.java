package com.example.Backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;  // Match the actual class name
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Model.Alerta;
import com.example.Backend.Service.AlertaServicie;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {
    private final AlertaServicie alertaServicie;

    public AlertaController(AlertaServicie alertaServicie) {
        this.alertaServicie = alertaServicie;
    }

    @PostMapping
    public ResponseEntity<String> enviarAlertas(@RequestBody List<Alerta> alertas) {
        try {
            alertaServicie.procesarAlertas(alertas);  // Changed to match the service method
            return ResponseEntity.ok("Alertas procesadas correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error procesando alertas: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Alerta>> obtenerAlertas(
            @RequestParam(required = false) String pacienteId,
            @RequestParam(required = false) String tipo) {
        try {
            List<Alerta> alertas = alertaServicie.obtenerAlertas(pacienteId, tipo);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}