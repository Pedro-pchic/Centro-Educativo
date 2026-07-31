package com.umg.sgau.estudiante.mapper;

import com.umg.sgau.estudiante.dto.EstudianteRequest;
import com.umg.sgau.estudiante.dto.EstudianteResponse;
import com.umg.sgau.estudiante.entity.EstudianteEntity;

public class EstudianteMapper {

    public static EstudianteEntity toEntity(EstudianteRequest request) {
        if (request == null) {
            return null;
        }

        EstudianteEntity entity = new EstudianteEntity();
        entity.setCarne(request.getCarne());
        entity.setNombre(request.getNombre());
        entity.setApellido(request.getApellido());
        entity.setEmail(request.getEmail());
        entity.setTelefono(request.getTelefono());
        entity.setFechaNacimiento(request.getFechaNacimiento());
        entity.setActivo(true);

        return entity;
    }

    public static EstudianteResponse toResponse(EstudianteEntity entity) {
        if (entity == null) {
            return null;
        }

        return EstudianteResponse.builder()
                .id(entity.getId())
                .carne(entity.getCarne())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .fechaNacimiento(entity.getFechaNacimiento())
                .activo(entity.getActivo())
                .build();
    }
}
