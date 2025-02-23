package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Model.Alerta;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
