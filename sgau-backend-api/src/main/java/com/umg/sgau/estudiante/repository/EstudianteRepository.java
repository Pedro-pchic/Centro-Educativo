package com.umg.sgau.estudiante.repository;

import com.umg.sgau.estudiante.entity.EstudianteEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {

    Optional<EstudianteEntity> findByEmail(String email);

    Optional<EstudianteEntity> findByCodigoEstudiante(String codigoEstudiante);

    boolean existsByEmail(String email);

    boolean existsByCodigoEstudiante(String codigoEstudiante);
}
