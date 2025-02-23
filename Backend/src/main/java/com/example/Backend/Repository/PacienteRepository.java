package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Backend.Model.Paciente;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByRut(String rut);
}
