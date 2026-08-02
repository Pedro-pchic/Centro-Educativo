package com.umg.sgau.nota.service;

import com.umg.sgau.nota.dto.NotaRequestDTO;
import com.umg.sgau.nota.dto.NotaResponseDTO;

import java.util.List;

public interface NotaService {

    // Registrar calificaciones
	NotaResponseDTO registrarNota(NotaRequestDTO nuevaNota);

    // Actualizar calificaciones
	NotaResponseDTO actualizarNota(Long id, NotaRequestDTO notaActualizada);

    void eliminar(Long id);

    // Consultar notas por diferentes criterios
    List<NotaResponseDTO> obtenerTodas();
    NotaResponseDTO obtenerPorId(Long id);
    List<NotaResponseDTO> obtenerPorEstudiante(Long estudianteId);
    List<NotaResponseDTO> obtenerPorCurso(Long cursoId);
    List<NotaResponseDTO> obtenerPorInscripcion(Long inscripcionId);
    NotaResponseDTO obtenerPorEstudianteYCurso(Long estudianteId, Long cursoId);

    // Calcular automáticamente el promedio general
    Double calcularPromedioEstudiante(Long estudianteId);
}
