package com.umg.sgau.nota.repository;

import com.umg.sgau.nota.entity.NotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<NotaEntity, Long> {
	
    // Buscar las notas asociadas a una inscripción específica
    List<NotaEntity> findByInscripcionId(Long inscripcionId);
}
