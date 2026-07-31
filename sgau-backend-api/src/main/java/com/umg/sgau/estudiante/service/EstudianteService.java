package com.umg.sgau.estudiante.service;

import com.umg.sgau.estudiante.dto.EstudianteRequestDTO;
import com.umg.sgau.estudiante.dto.EstudianteResponseDTO;

import java.util.List;

public interface EstudianteService {

    EstudianteResponseDTO crear(EstudianteRequestDTO request);

    List<EstudianteResponseDTO> listar();

    EstudianteResponseDTO buscarPorId(Long id);

    EstudianteResponseDTO actualizar(Long id, EstudianteRequestDTO request);

    void eliminar(Long id);
}
