package com.umg.sgau.colegiatura.repository;

import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.estudiante.entity.EstudianteEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColegiaturaRepository extends JpaRepository<ColegiaturaEntity, Long> {

    List<ColegiaturaEntity> findByEstudiante(EstudianteEntity estudiante);

    List<ColegiaturaEntity> findByCiclo(String ciclo);

    List<ColegiaturaEntity> findByMes(String mes);

}