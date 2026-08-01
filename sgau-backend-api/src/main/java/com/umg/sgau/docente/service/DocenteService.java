package com.umg.sgau.docente.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.umg.sgau.docente.entity.DocenteEntity;

public interface DocenteService {
	
	DocenteEntity registrarDocente(DocenteEntity nuevoDocente);
	
	List<DocenteEntity> obtenerDocentesActivos();
	
	DocenteEntity buscarPorDpi(String dpi);
	
	DocenteEntity buscarPorId(String id);
	
	List <DocenteEntity> obtenerTodos();
	
	List<DocenteEntity> buscarPorEspecialidad(String especialidad);
	
	DocenteEntity actualizar(Long id, DocenteEntity docente);

	void eliminar(Long id);
	
	void habilitar(Long id);
	
	Page<DocenteEntity> obtenerTodosPaginados(Pageable pageable);
	
	Page<DocenteEntity> buscarPorFiltros(String filtro, Pageable pageable);
}