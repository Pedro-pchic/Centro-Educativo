package com.umg.sgau.estudiante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umg.sgau.estudiante.entity.Estudiante;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCarnet(String carnet);

    Optional<Estudiante> findByEmail(String email);

    boolean existsByCarnet(String carnet);

    boolean existsByEmail(String email);
}
