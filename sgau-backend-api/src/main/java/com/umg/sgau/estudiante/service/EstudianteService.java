package com.umg.sgau.estudiante.service;

import com.umg.sgau.estudiante.entity.EstudianteEntity;

import java.util.List;

public interface EstudianteService {

    EstudianteEntity crear(EstudianteEntity estudiante);

    EstudianteEntity obtenerPorId(Long id);

    List<EstudianteEntity> obtenerTodos();

    EstudianteEntity actualizar(Long id, EstudianteEntity estudiante);

    void eliminar(Long id);
}