package com.example.Backend.Service.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Backend.DTO.PacienteDTO;
import com.example.Backend.Exceptions.NotFoundException;
import com.example.Backend.Model.Paciente;
import com.example.Backend.Repository.PacienteRepository;
import com.example.Backend.Service.PacienteService;

@Service
public class PacienteServiceImpl implements PacienteService{
    
     private final PacienteRepository pacienteRepository;
    
    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }


    // Implementar los métodos de la interfaz PacienteService

    // Método para obtener todos los pacientes
    @Override
    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }


    // Método para obtener un paciente por su id
    @Override
    public Optional<Paciente> getPacienteById(Long id) {
        return pacienteRepository.findById(id);
    }

    @Override
    public Optional<Paciente> getPacienteByRut(String rut) {
        return pacienteRepository.findByRut(rut);
    }

    // Método para crear un paciente
    @Override
    public Paciente createPaciente(Paciente paciente)throws IOException {     
        return pacienteRepository.save(paciente);
    }


    // Método para actualizar un paciente
    @Override
    public Paciente updatePaciente(String rut,Paciente paciente){
        if(!pacienteRepository.findByRut(rut).isPresent()) {
            throw new NotFoundException("Paciente no encontrado con RUT: " + rut);
        }
        paciente.setrut(rut);
        return pacienteRepository.save(paciente);
    }

    // metodo para elimar un paciente
    @Override
    public void deletePaciente(Paciente paciente) throws IOException {
        Optional<Paciente> existingPaciente = pacienteRepository.findByRut(paciente.getrut());
        if(existingPaciente.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con RUT: " + paciente.getrut());
        }
        pacienteRepository.delete(existingPaciente.get());
    }

    @Override
    public Map<String, Object> generarReporteSaludCompleto(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findByRut(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con RUT: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("informacionBasica", Map.of(
            "nombreCompleto", paciente.getNombre() + " " + paciente.getApellido(),
            "edad", paciente.getEdad(),
            "habitacion", paciente.getHabitacion(),
            "condicion", paciente.getCondicion()
        ));
        
        reporte.put("estadoActual", Map.of(
            "presionArterial", String.format("%.0f/%.0f mmHg", 
                paciente.getPresionSistolica(),
                paciente.getPresionDiastolica()),
            "frecuenciaCardiaca", String.format("%.0f lpm", 
                paciente.getFrecuenciaCardiaca()),
            "oxigeno", String.format("%.1f%%", 
                paciente.getOxigeno()),
            "temperatura", String.format("%.1f°C", 
                paciente.getTemperatura())
        ));
        
        return reporte;
    }

    @Override
    public Map<String, Object> obtenerEstadisticasSalud(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findByRut(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con RUT: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("promedioPresion", calcularPromedio(paciente.getHistorialPresion()));
        estadisticas.put("promedioFrecuencia", calcularPromedio(paciente.getHistorialFrecuencia()));
        estadisticas.put("tendencias", Map.of(
            "presion", calcularTendencia(paciente.getHistorialPresion()),
            "frecuencia", calcularTendencia(paciente.getHistorialFrecuencia())
        ));
        return estadisticas;
    }

    @Override
    public Map<String, Object> obtenerAlertasSaludDetalladas(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findByRut(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con RUT: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        Map<String, Object> alertas = new HashMap<>();
        alertas.put("presionArterial", evaluarAlertaPresion(paciente.getPresionArterial()));
        alertas.put("frecuenciaCardiaca", evaluarAlertaFrecuencia(paciente.getFrecuenciaCardiaca()));
        alertas.put("temperatura", evaluarAlertaTemperatura(paciente.getTemperatura()));
        return alertas;
    }

    @Override
    public Map<String, Object> obtenerSeguimientoCondicionCronica(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findByRut(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con RUT: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        return Map.of(
            "condicion", paciente.getCondicion(),
            "evolucion", paciente.getEvolucionCondicion(),
            "medicamentos", paciente.getMedicamentos(),
            "ultimosControles", paciente.getUltimosControles()
        );
    }

    @Override
    public List<String> obtenerHistorialMedico(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findByRut(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con RUT: " + rut);
        }
        return pacienteOpt.get().getHistorialMedico();
    }

    @Override
    public byte[] exportarReporte(String rut, String formato) {
        Map<String, Object> reporte = generarReporteSaludCompleto(rut);
        return reporte.toString().getBytes();
    }

    private double calcularPromedio(List<Double> valores) {
        return valores.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }

    private String calcularTendencia(List<Double> valores) {
        if (valores.size() < 2) return "Estable";
        double ultimo = valores.get(valores.size() - 1);
        double penultimo = valores.get(valores.size() - 2);
        return ultimo > penultimo ? "Ascendente" : ultimo < penultimo ? "Descendente" : "Estable";
    }

    private Map<String, String> evaluarAlertaPresion(double presion) {
        Map<String, String> alerta = new HashMap<>();
        if (presion > 140) {
            alerta.put("nivel", "ALTO");
            alerta.put("recomendacion", "Consultar inmediatamente con médico");
        } else if (presion < 90) {
            alerta.put("nivel", "BAJO");
            alerta.put("recomendacion", "Monitorear y consultar si persiste");
        } else {
            alerta.put("nivel", "NORMAL");
            alerta.put("recomendacion", "Continuar monitoreo regular");
        }
        return alerta;
    }

    private Map<String, String> evaluarAlertaFrecuencia(double frecuencia) {
        Map<String, String> alerta = new HashMap<>();
        if (frecuencia > 100) {
            alerta.put("nivel", "ALTO");
            alerta.put("recomendacion", "Consultar con médico si persiste");
        } else if (frecuencia < 60) {
            alerta.put("nivel", "BAJO");
            alerta.put("recomendacion", "Monitorear y consultar si hay síntomas");
        } else {
            alerta.put("nivel", "NORMAL");
            alerta.put("recomendacion", "Continuar monitoreo regular");
        }
        return alerta;
    }

    private Map<String, String> evaluarAlertaTemperatura(double temperatura) {
        Map<String, String> alerta = new HashMap<>();
        if (temperatura > 38) {
            alerta.put("nivel", "FIEBRE");
            alerta.put("recomendacion", "Tomar antipirético y consultar si persiste");
        } else if (temperatura < 36) {
            alerta.put("nivel", "BAJA");
            alerta.put("recomendacion", "Abrigarse y monitorear");
        } else {
            alerta.put("nivel", "NORMAL");
            alerta.put("recomendacion", "Continuar monitoreo regular");
        }
        return alerta;
    }
}
