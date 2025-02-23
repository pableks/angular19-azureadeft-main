package com.example.Backend.Service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Backend.Model.SignosVitales;
import com.example.Backend.Repository.SignosVitalesRepository;
import com.example.Backend.Service.Signos_VitalesRep;


@Service
public class Signos_VitalesRepImpl implements Signos_VitalesRep{
    
    private final SignosVitalesRepository signosvitalesRepository;

    public Signos_VitalesRepImpl(SignosVitalesRepository signosvitalesRepository)
    {
        this.signosvitalesRepository = signosvitalesRepository;
    }

    // Implementar los métodos de la interfaz signos vitales

    // Método para obtener todos los signos vitales
    @Override
    public List<SignosVitales> getAllSignosVitales() {
        return signosvitalesRepository.findAll();
    }

    @Override
    public SignosVitales saveSignosVitales(SignosVitales signosVitales) {
        return signosvitalesRepository.save(signosVitales);
    }

    @Override
    public List<SignosVitales> findByPacienteId(Long pacienteId) {
        return signosvitalesRepository.findByPacienteId(pacienteId);
    }

    @Override
    public void deleteAll(List<SignosVitales> existingRecords) {
        signosvitalesRepository.deleteAll(existingRecords);
    }
}
