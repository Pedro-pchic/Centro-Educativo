package com.umg.sgau.curso.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umg.sgau.curso.entity.CursoEntity;

@Repository
public interface CursoRepository extends JpaRepository<CursoEntity, Long> {

    Optional<CursoEntity> findByIdAndActivoTrue(Long id);

    Optional<CursoEntity> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    List<CursoEntity> findAllByActivoTrue();

    List<CursoEntity> findAllByActivoFalse();

    List<CursoEntity> findAllByCarreraIdAndActivoTrue(Long carreraId);
}

