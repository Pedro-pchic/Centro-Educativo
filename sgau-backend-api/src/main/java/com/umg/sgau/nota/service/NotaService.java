package com.umg.sgau.nota.service;

import com.umg.sgau.inscripcion.entity.InscripcionEntity;
import com.umg.sgau.nota.dto.NotaRequestDTO;
import com.umg.sgau.nota.dto.NotaResponseDTO;
import com.umg.sgau.nota.entity.NotaEntity;

import java.util.List;

public interface NotaService {

    // Registrar calificaciones
	NotaResponseDTO registrarNota(NotaRequestDTO nuevaNota);

    // Actualizar calificaciones
	NotaResponseDTO actualizarNota(Long id, NotaRequestDTO notaActualizada);

    // Consultar notas por diferentes criterios
    List<NotaResponseDTO> obtenerTodas();
    List<NotaResponseDTO> obtenerPorEstudiante(Long estudianteId);
    List<NotaResponseDTO> obtenerPorCurso(Long cursoId);
    List<NotaResponseDTO> obtenerPorInscripcion(Long inscripcionId);
    NotaResponseDTO obtenerPorEstudianteYCurso(Long estudianteId, Long cursoId);

    // Calcular automáticamente el promedio general
    Double calcularPromedioEstudiante(Long estudianteId);
}