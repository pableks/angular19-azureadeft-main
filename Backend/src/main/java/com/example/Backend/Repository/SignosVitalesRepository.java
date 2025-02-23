package com.example.Backend.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Model.SignosVitales;

@Repository
public interface SignosVitalesRepository extends JpaRepository<SignosVitales, Long> {
    List<SignosVitales> findByPacienteIdAndFechaRegistroBetween(Long pacienteId, LocalDateTime inicio, LocalDateTime fin);
    List<SignosVitales> findByEsAlertaTrue();
    List<SignosVitales> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);
    List<SignosVitales> findByPacienteId(Long pacienteId);

}
