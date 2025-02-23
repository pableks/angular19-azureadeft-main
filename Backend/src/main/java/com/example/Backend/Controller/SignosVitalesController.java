package com.example.Backend.Controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Model.SignosVitales;
import com.example.Backend.Service.Signos_VitalesRep;
import com.example.Backend.Service.AlertaServicie;
import com.example.Backend.Service.IndicadorService;
import com.example.Backend.Model.Alerta;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class SignosVitalesController {
    
    private final Signos_VitalesRep signosvitales_resp;
    private final IndicadorService indicadorService;
    private final AlertaServicie alertaServicie;

    //constructor
    
public SignosVitalesController(Signos_VitalesRep signosvitales_resp, 
                              IndicadorService indicadorService,
                              AlertaServicie alertaServicie) {
    this.signosvitales_resp = signosvitales_resp;
    this.indicadorService = indicadorService;
    this.alertaServicie = alertaServicie;
}


    //listar signos
      //Listar todos los pacientes
    @GetMapping("/signos-vitales")
    public List<SignosVitales> getAllSignosVitales() {
        return signosvitales_resp.getAllSignosVitales();
    }

    @PostMapping("/signos-vitales")
    public ResponseEntity<Map<String, Object>> registrarSignosVitales(@RequestBody SignosVitales signosVitales) {
        try {
            // Set the current date and time
            signosVitales.setFechaRegistro(LocalDateTime.now());
            
            // Process alerts
            List<String> alertas = indicadorService.getAlertasMedicas(
                signosVitales.getPacienteId(),
                Double.parseDouble(signosVitales.getPresionArterial().split("/")[0]), // sistólica
                Double.parseDouble(signosVitales.getPresionArterial().split("/")[1]), // diastólica
                signosVitales.getSaturacionOxigeno(),
                signosVitales.getFrecuenciaCardiaca(),
                signosVitales.getTemperatura()
            );
            
            // Set alert status
            signosVitales.setEsAlerta(!alertas.isEmpty());
            
            // Save the signos vitales
            SignosVitales savedSignos = signosvitales_resp.saveSignosVitales(signosVitales);
            
            String nivelRiesgo = indicadorService.evaluarNivelRiesgo(alertas);
            
            Map<String, Object> response = new HashMap<>();
            response.put("signosVitales", savedSignos);
            response.put("alertas", alertas);
            response.put("nivelRiesgo", nivelRiesgo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/signos-vitales/latest")
    public ResponseEntity<Map<String, Object>> registrarUltimosSignosVitales(@RequestBody SignosVitales signosVitales) {
        try {
            // Set the current date and time
            signosVitales.setFechaRegistro(LocalDateTime.now());
            
            // Find and delete existing records for this patient
            List<SignosVitales> existingRecords = signosvitales_resp.findByPacienteId(signosVitales.getPacienteId());
            if (!existingRecords.isEmpty()) {
                signosvitales_resp.deleteAll(existingRecords);
            }
            
            // Process alerts
            List<String> alertas = new ArrayList<>();
            
            // Check vital signs and add alerts
            if (signosVitales.getFrecuenciaCardiaca() < 60 || signosVitales.getFrecuenciaCardiaca() > 100) {
                alertas.add("Frecuencia cardíaca anormal: " + signosVitales.getFrecuenciaCardiaca());
            }
            
            if (signosVitales.getTemperatura() < 36.0 || signosVitales.getTemperatura() > 38.0) {
                alertas.add("Temperatura anormal: " + signosVitales.getTemperatura());
            }
            
            if (signosVitales.getSaturacionOxigeno() < 95) {
                alertas.add("Saturación de oxígeno baja: " + signosVitales.getSaturacionOxigeno());
            }
            
            // Parse and check blood pressure
            String[] presionValues = signosVitales.getPresionArterial().split("/");
            if (presionValues.length == 2) {
                int sistolica = Integer.parseInt(presionValues[0]);
                int diastolica = Integer.parseInt(presionValues[1]);
                if (sistolica > 140 || sistolica < 90 || diastolica > 90 || diastolica < 60) {
                    alertas.add("Presión arterial anormal: " + signosVitales.getPresionArterial());
                }
            }
            
            // Set alert status
            signosVitales.setEsAlerta(!alertas.isEmpty());
            
            // Save the new record
            SignosVitales savedSignos = signosvitales_resp.saveSignosVitales(signosVitales);
            
            // Process alerts through RabbitMQ if any exist
            if (!alertas.isEmpty()) {
                Alerta alerta = new Alerta();
                alerta.setPacienteId(signosVitales.getPacienteId());
                alerta.setFecha(LocalDateTime.now());
                alerta.setDescripcion(String.join(", ", alertas));
                alerta.setTipo("ALERTA");
                
                alertaServicie.procesarAlertas(List.of(alerta));
            }
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("signosVitales", savedSignos);
            response.put("alertas", alertas);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al procesar signos vitales: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
