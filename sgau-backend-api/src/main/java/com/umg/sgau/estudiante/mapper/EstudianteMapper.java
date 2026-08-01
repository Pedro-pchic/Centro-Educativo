package com.umg.sgau.estudiante.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.umg.sgau.estudiante.dto.EstudianteRequestDTO;
import com.umg.sgau.estudiante.dto.EstudianteResponseDTO;
import com.umg.sgau.estudiante.entity.EstudianteEntity;

public class EstudianteMapper {

    private EstudianteMapper() {
    }

    public static EstudianteEntity aEntidad(EstudianteRequestDTO dto) {

        EstudianteEntity estudiante = new EstudianteEntity();

        estudiante.setCodigoEstudiante(dto.getCodigoEstudiante());
        estudiante.setEmail(dto.getEmail());
        estudiante.setNombre(dto.getNombre());
        estudiante.setApellido(dto.getApellido());

        return estudiante;
    }


    public static EstudianteResponseDTO aResponseDTO(
            EstudianteEntity estudiante) {

        EstudianteResponseDTO dto = new EstudianteResponseDTO();

        dto.setId(estudiante.getId());
        dto.setCodigoEstudiante(
                estudiante.getCodigoEstudiante()
        );
        dto.setEmail(estudiante.getEmail());
        dto.setNombre(estudiante.getNombre());
        dto.setApellido(estudiante.getApellido());
        dto.setActivo(estudiante.getActivo());
        dto.setFechaCreacion(
                estudiante.getFechaCreacion()
        );

        return dto;
    }


    public static List<EstudianteResponseDTO> aResponseDTOList(
            List<EstudianteEntity> estudiantes) {

        return estudiantes.stream()
                .map(EstudianteMapper::aResponseDTO)
                .collect(Collectors.toList());
    }
}
