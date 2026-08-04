package com.umg.sgau.colegiatura.repository;

import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.estudiante.entity.EstudianteEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ColegiaturaRepository extends JpaRepository<ColegiaturaEntity, Long> {


    // Solo colegiaturas activas de un estudiante
    List<ColegiaturaEntity> findByEstudianteAndActivoTrue(
            EstudianteEntity estudiante
    );


    // Solo ciclo activo
    List<ColegiaturaEntity> findByCicloAndActivoTrue(
            String ciclo
    );


    // Solo mes activo
    List<ColegiaturaEntity> findByMesAndActivoTrue(
            String mes
    );


    // Buscar por ID solamente si está activo
    Optional<ColegiaturaEntity> findByIdAndActivoTrue(
            Long id
    );


    // Evitar duplicados activos
    boolean existsByEstudianteAndMesAndCicloAndActivoTrue(
            EstudianteEntity estudiante,
            String mes,
            String ciclo
    );


	List<ColegiaturaEntity> findAllByActivoTrue();


}