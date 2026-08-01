package com.umg.sgau.carrera.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umg.sgau.carrera.entity.CarreraEntity;

@Repository
public interface CarreraRepository extends JpaRepository<CarreraEntity, Long> {

    Optional<CarreraEntity> findByIdAndActivoTrue(Long id);

    Optional<CarreraEntity> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    List<CarreraEntity> findAllByActivoTrue();

    List<CarreraEntity> findAllByActivoFalse();
}
