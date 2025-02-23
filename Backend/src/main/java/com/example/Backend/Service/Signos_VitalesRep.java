package com.example.Backend.Service;

import java.util.List;

import com.example.Backend.Model.SignosVitales;

public interface Signos_VitalesRep {

     //listar todos los signos vitales
    List<SignosVitales> getAllSignosVitales();
    SignosVitales saveSignosVitales(SignosVitales signosVitales);
    List<SignosVitales> findByPacienteId(Long pacienteId);
    void deleteAll(List<SignosVitales> existingRecords);
}
