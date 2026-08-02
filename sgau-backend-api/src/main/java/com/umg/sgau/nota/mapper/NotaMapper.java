package com.umg.sgau.nota.mapper;

import com.umg.sgau.inscripcion.entity.InscripcionEntity;
import com.umg.sgau.nota.dto.NotaRequestDTO;
import com.umg.sgau.nota.dto.NotaResponseDTO;
import com.umg.sgau.nota.entity.NotaEntity;

import java.util.List;
import java.util.stream.Collectors;

public class NotaMapper {

    private NotaMapper() {
    }

    public static NotaEntity aEntidad(
            NotaRequestDTO dto,
            InscripcionEntity inscripcion) {

        NotaEntity nota = new NotaEntity();

        nota.setCicloAcademico(dto.getCicloAcademico());
        nota.setZona(dto.getZona());
        nota.setExamenFinal(dto.getExamenFinal());
        nota.setEstado(dto.getEstado());
        nota.setFechaRegistro(dto.getFechaRegistro());
        nota.setActivo(true);
        nota.setInscripcion(inscripcion);

        return nota;
    }

    public static NotaResponseDTO aResponseDTO(NotaEntity nota) {
        NotaResponseDTO dto = new NotaResponseDTO();
        dto.setId(nota.getId());
        dto.setCicloAcademico(nota.getCicloAcademico());
        dto.setZona(nota.getZona());
        dto.setExamenFinal(nota.getExamenFinal());
        dto.setNotaFinal(nota.getNotaFinal());
        dto.setEstado(nota.getEstado());
        dto.setActivo(nota.getActivo());
        dto.setFechaRegistro(nota.getFechaRegistro());
               
        if (nota.getInscripcion() != null) {
        	InscripcionEntity ins = nota.getInscripcion();
            dto.setInscripcionId(ins.getId());
            
            if (ins.getEstudiante() != null) {
                dto.setEstudianteId(ins.getEstudiante().getId());
                dto.setEstudianteNombre(ins.getEstudiante().getNombre());
            }

            if (ins.getCurso() != null) {
                dto.setCursoId(ins.getCurso().getId());
                dto.setCursoNombre(ins.getCurso().getNombre());
            }
        }

        return dto;
    }

    public static List<NotaResponseDTO> aResponseDTOList(List<NotaEntity> notas) {
        return notas.stream()
                .map(NotaMapper::aResponseDTO)
                .collect(Collectors.toList());
    }
}
