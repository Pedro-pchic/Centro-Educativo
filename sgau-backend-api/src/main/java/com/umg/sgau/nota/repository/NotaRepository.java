package com.umg.sgau.nota.repository;

import com.umg.sgau.nota.entity.NotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<NotaEntity, Long> {

    List<NotaEntity> findByActivoTrue();

    Optional<NotaEntity> findByIdAndActivoTrue(Long id);

    List<NotaEntity> findByActivoTrueAndInscripcion_Estudiante_Id(Long estudianteId);

    List<NotaEntity> findByActivoTrueAndInscripcion_Curso_Id(Long cursoId);

    List<NotaEntity> findByActivoTrueAndInscripcion_Id(Long inscripcionId);

    List<NotaEntity> findByActivoTrueAndInscripcion_Estudiante_IdAndInscripcion_Curso_Docente_Id(
            Long estudianteId, Long docenteId);

    Optional<NotaEntity> findFirstByActivoTrueAndInscripcion_Estudiante_IdAndInscripcion_Curso_Id(
            Long estudianteId, Long cursoId);

    boolean existsByInscripcionIdAndActivoTrue(Long inscripcionId);
}
