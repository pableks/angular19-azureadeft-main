package com.example.Backend.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Model.Paciente;
import com.example.Backend.Service.IndicadorService;
import com.example.Backend.Service.PacienteService;

@RestController
@CrossOrigin
@RequestMapping("/paciente")
public class PacienteController {
    
     private final PacienteService pacienteService;
     private final IndicadorService indicadorService;


    //contructor
    public PacienteController(PacienteService pacienteService, IndicadorService indicadorService) {
        this.pacienteService = pacienteService;
        this.indicadorService = indicadorService;
    }


    //crear paciente
    @PostMapping("/crear")
    public ResponseEntity<Paciente> createPaciente(@RequestBody Paciente paciente) {
        try {
       
            Paciente createPaciente = pacienteService.createPaciente(paciente);
            return new ResponseEntity<>(createPaciente, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
    }

    @PutMapping("/{rut}")
    public ResponseEntity<Paciente> updatePaciente(@PathVariable String rut, @RequestBody Paciente paciente) {
        try {
            // Actualizar paciente en la base de datos
            Paciente updatePaciente = pacienteService.updatePaciente(rut, paciente);
            
            // Evaluar alertas médicas con los valores del paciente
            List<String> alertas = indicadorService.getAlertasMedicas(
                Long.parseLong(rut.replaceAll("[^0-9]", "")),  // Convert RUT directly to Long
                paciente.getPresionSistolica(),
                paciente.getPresionDiastolica(),
                paciente.getOxigeno(),
                paciente.getFrecuenciaCardiaca(),
                paciente.getTemperatura()
            );
    
            // Retornar respuesta con el paciente actualizado
            return new ResponseEntity<>(updatePaciente, HttpStatus.OK);
    
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //eliminar paciente
    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> deletePaciente(@PathVariable String rut)throws IOException {
        try {

           Optional<Paciente> paciente = pacienteService.getPacienteByRut(rut);
            
           if (paciente.isPresent()) {

                pacienteService.deletePaciente(paciente.get());
                return new ResponseEntity<>(HttpStatus.OK);
            
            } else {
            
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Crear paciente de ejemplo
    @PostMapping("/crear-ejemplo")
    public ResponseEntity<Paciente> crearPacienteEjemplo() {
        Paciente paciente = new Paciente();
        paciente.setrut("11432567-8");
        paciente.setNombre("Juan");
        paciente.setApellido("Pérez");
        paciente.setEdad(45);
        paciente.setHabitacion("301A");
        paciente.setFrecuenciaCardiaca(85.0);
        paciente.setOxigeno(96.5);
        paciente.setPresionSistolica(120.0);
        paciente.setPresionDiastolica(80.0);
        paciente.setTemperatura(37.2);
        paciente.setCondicion("Hipertensión controlada");
        paciente.setUltimoControl(LocalDateTime.parse("2023-10-15T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        paciente.setObservaciones("Paciente estable, continuar tratamiento");
        paciente.setHistorialMedico(List.of("Hipertensión desde 2018"));
        paciente.setMedicamentos(List.of("Losartán 50mg", "Atorvastatina 20mg"));
        
        try {
            Paciente created = pacienteService.createPaciente(paciente);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //Listar todos los pacientes
    @GetMapping("/listado")
    public List<Paciente> getAllPacientes() {
        return pacienteService.getAllPacientes();
    }


    //Buscar paciente por id
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> getPacienteById(@PathVariable Long id) {
        return pacienteService.getPacienteById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Obtener reporte completo de salud mejorado
    @GetMapping("/reporte-salud/{rut}")
    public ResponseEntity<Map<String, Object>> getReporteSalud(@PathVariable String rut) {
        try {
            Map<String, Object> reporte = pacienteService.generarReporteSaludCompleto(rut);
            return new ResponseEntity<>(reporte, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Obtener estadísticas de salud
    @GetMapping("/estadisticas-salud/{rut}")
    public ResponseEntity<Map<String, Object>> getEstadisticasSalud(@PathVariable String rut) {
        try {
            Map<String, Object> estadisticas = pacienteService.obtenerEstadisticasSalud(rut);
            return new ResponseEntity<>(estadisticas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Obtener alertas de salud mejoradas
    @GetMapping("/alertas/{rut}")
    public ResponseEntity<Map<String, Object>> getAlertasSalud(@PathVariable String rut) {
        try {
            Map<String, Object> alertas = pacienteService.obtenerAlertasSaludDetalladas(rut);
            return new ResponseEntity<>(alertas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Seguimiento de condiciones crónicas
    @GetMapping("/seguimiento-cronico/{rut}")
    public ResponseEntity<Map<String, Object>> getSeguimientoCronico(@PathVariable String rut) {
        try {
            Map<String, Object> seguimiento = pacienteService.obtenerSeguimientoCondicionCronica(rut);
            return new ResponseEntity<>(seguimiento, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Obtener historial médico completo
    @GetMapping("/historial-medico/{rut}")
    public ResponseEntity<List<String>> getHistorialMedico(@PathVariable String rut) {
        try {
            List<String> historial = pacienteService.obtenerHistorialMedico(rut);
            return new ResponseEntity<>(historial, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Exportar reporte en diferentes formatos
    @GetMapping("/exportar-reporte/{rut}")
    public ResponseEntity<byte[]> exportarReporte(
            @PathVariable String rut,
            @RequestParam(defaultValue = "pdf") String formato) {
        try {
            byte[] reporte = pacienteService.exportarReporte(rut, formato);
            HttpHeaders headers = new HttpHeaders();
            
            if (formato.equalsIgnoreCase("pdf")) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("filename", "reporte-salud.pdf");
            } else if (formato.equalsIgnoreCase("csv")) {
                headers.setContentType(MediaType.TEXT_PLAIN);
                headers.setContentDispositionFormData("filename", "reporte-salud.csv");
            }
            
            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Paciente> getPacienteByRut(@PathVariable String rut) {
        return pacienteService.getPacienteByRut(rut)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
