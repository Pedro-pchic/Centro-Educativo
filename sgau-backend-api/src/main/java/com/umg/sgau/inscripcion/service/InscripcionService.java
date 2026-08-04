package com.umg.sgau.inscripcion.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.umg.sgau.inscripcion.dto.InscripcionRequestDTO;
import com.umg.sgau.inscripcion.dto.InscripcionResponseDTO;

public interface InscripcionService {

    InscripcionResponseDTO crear(InscripcionRequestDTO request);

    InscripcionResponseDTO obtenerPorId(Long id);

    List<InscripcionResponseDTO> obtenerTodas();

    Page<InscripcionResponseDTO> obtenerTodasPaginadas(int pagina, int tamanio);

    List<InscripcionResponseDTO> obtenerPorEstudiante(Long estudianteId);

    List<InscripcionResponseDTO> obtenerPorCurso(Long cursoId);

    InscripcionResponseDTO actualizar(Long id, InscripcionRequestDTO request);

    void eliminar(Long id);
}
