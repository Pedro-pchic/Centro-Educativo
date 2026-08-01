package com.umg.sgau.inscripcion.mapper;

import org.springframework.stereotype.Component;

import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.inscripcion.dto.InscripcionRequestDTO;
import com.umg.sgau.inscripcion.dto.InscripcionResponseDTO;
import com.umg.sgau.inscripcion.entity.InscripcionEntity;

@Component
public class InscripcionMapper {

    public InscripcionEntity aEntidad(
            InscripcionRequestDTO request,
            EstudianteEntity estudiante,
            CursoEntity curso) {
        InscripcionEntity entity = new InscripcionEntity();
        entity.setEstudiante(estudiante);
        entity.setCurso(curso);
        if (request.getFechaInscripcion() != null) {
            entity.setFechaInscripcion(request.getFechaInscripcion());
        }
        entity.setActivo(true);
        return entity;
    }

    public InscripcionResponseDTO aResponseDTO(InscripcionEntity entity) {
        InscripcionResponseDTO response = new InscripcionResponseDTO();
        response.setId(entity.getId());
        response.setEstudianteId(entity.getEstudiante().getId());
        response.setCodigoEstudiante(entity.getEstudiante().getCodigoEstudiante());
        response.setNombreEstudiante(construirNombreEstudiante(entity.getEstudiante()));
        response.setCursoId(entity.getCurso().getId());
        response.setNombreCurso(entity.getCurso().getNombre());
        response.setFechaInscripcion(entity.getFechaInscripcion());
        response.setActivo(entity.getActivo());
        response.setFechaCreacion(entity.getFechaCreacion());
        response.setFechaActualizacion(entity.getFechaActualizacion());
        return response;
    }

    public void actualizarEntidad(
            InscripcionEntity entity,
            InscripcionRequestDTO request,
            EstudianteEntity estudiante,
            CursoEntity curso) {
        entity.setEstudiante(estudiante);
        entity.setCurso(curso);
        if (request.getFechaInscripcion() != null) {
            entity.setFechaInscripcion(request.getFechaInscripcion());
        }
    }

    private String construirNombreEstudiante(EstudianteEntity estudiante) {
        String nombre = estudiante.getNombre() == null ? "" : estudiante.getNombre().trim();
        String apellido = estudiante.getApellido() == null ? "" : estudiante.getApellido().trim();
        return (nombre + " " + apellido).trim();
    }
}
