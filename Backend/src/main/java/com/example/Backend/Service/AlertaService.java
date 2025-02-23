package com.example.Backend.Service;

import java.util.List;

import com.example.Backend.Model.Alerta;

public interface AlertaService {
    void procesarAlertas(List<Alerta> alertas);
    List<Alerta> obtenerAlertas(String pacienteId, String tipo);
}
