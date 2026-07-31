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
	
	Optional<DocenteEntity> findByDpi(String dpi);
	
	Optional<DocenteEntity> findByEmailInstitucional(String emailInstitucional);
	
	List<DocenteEntity> findByActivoTrue();
	
	List<DocenteEntity> findByEspecialidadContainingIgnoreCase(String especialidad);
	

	@Query("SELECT d FROM DocenteEntity d WHERE " +
	       "LOWER(d.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
	       "LOWER(d.apellido) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
	       "LOWER(d.emailInstitucional) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
	       "LOWER(d.especialidad) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
	       "d.dpi LIKE CONCAT('%', :filtro, '%')")
	Page<DocenteEntity> buscarPorMultiplesFiltros(@Param("filtro") String filtro, Pageable pageable);

}