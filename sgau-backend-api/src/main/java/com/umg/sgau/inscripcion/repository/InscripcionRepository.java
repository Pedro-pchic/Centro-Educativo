package com.umg.sgau.inscripcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umg.sgau.inscripcion.entity.InscripcionEntity;

@Repository
public interface InscripcionRepository extends JpaRepository<InscripcionEntity, Long> {

    Optional<InscripcionEntity> findByIdAndActivoTrue(Long id);

    List<InscripcionEntity> findAllByActivoTrue();

    Page<InscripcionEntity> findAllByActivoTrue(Pageable pageable);

    List<InscripcionEntity> findByEstudiante_IdAndActivoTrue(Long estudianteId);

    List<InscripcionEntity> findByCurso_IdAndActivoTrue(Long cursoId);

    boolean existsByEstudiante_IdAndCurso_IdAndActivoTrue(Long estudianteId, Long cursoId);

    boolean existsByEstudiante_IdAndCurso_IdAndActivoTrueAndIdNot(
            Long estudianteId, Long cursoId, Long id);
}
