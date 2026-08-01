package com.umg.sgau.docente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.umg.sgau.docente.entity.DocenteEntity;

@Repository
public interface DocenteRepository extends JpaRepository<DocenteEntity, Long> {
	
	Optional<DocenteEntity> findById (Long id);
	
	Page<DocenteEntity> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailInstitucionalContainingIgnoreCaseOrEspecialidadContainingIgnoreCaseOrDpiContaining(
	    String nombre, 
	    String apellido, 
	    String email, 
	    String especialidad, 
	    String dpi, 
	    Pageable pageable
	);

}