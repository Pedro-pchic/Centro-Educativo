package com.umg.sgau.curso.service;

import java.util.List;

import com.umg.sgau.curso.dto.CursoRequestDTO;
import com.umg.sgau.curso.dto.CursoResponseDTO;

public interface CursoService {

    CursoResponseDTO crear(CursoRequestDTO request);

    CursoResponseDTO obtenerPorId(Long id);

    List<CursoResponseDTO> obtenerTodos();

    List<CursoResponseDTO> obtenerInactivos();

    List<CursoResponseDTO> obtenerPorCarrera(Long carreraId);

    CursoResponseDTO actualizar(
            Long id,
            CursoRequestDTO request
    );

    void eliminar(Long id);

    CursoResponseDTO restaurar(Long id);
}

