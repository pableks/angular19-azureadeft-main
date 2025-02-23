package com.example.Backend.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.Backend.Model.Paciente;

public interface PacienteService {
    
      //listar todos los pacientes
    List<Paciente> getAllPacientes();
    
    //buscar paciente por id
    Optional<Paciente> getPacienteById(Long id);
    
    //buscar paciente por rut
    Optional<Paciente> getPacienteByRut(String rut);
    
    //crear paciente
    Paciente createPaciente(Paciente idPaciente) throws IOException;

    //actualizar paciente
    Paciente updatePaciente(String rut,Paciente paciente);
    
    //eliminar paciente
    void deletePaciente(Paciente paciente) throws IOException;
    
    // Generar reporte de salud completo
    Map<String, Object> generarReporteSaludCompleto(String rut);
    
    // Obtener estadísticas de salud
    Map<String, Object> obtenerEstadisticasSalud(String rut);
    
    // Obtener alertas de salud mejoradas
    Map<String, Object> obtenerAlertasSaludDetalladas(String rut);
    
    // Seguimiento de condiciones crónicas
    Map<String, Object> obtenerSeguimientoCondicionCronica(String rut);
    
    // Obtener historial médico completo
    List<String> obtenerHistorialMedico(String rut);
    
    // Exportar reporte en diferentes formatos
    byte[] exportarReporte(String rut, String formato);
}
