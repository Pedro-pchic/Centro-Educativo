package com.umg.sgau.carrera.mapper;

import com.umg.sgau.carrera.dto.CarreraRequestDTO;
import com.umg.sgau.carrera.dto.CarreraResponseDTO;
import com.umg.sgau.carrera.entity.CarreraEntity;

public final class CarreraMapper {

    private CarreraMapper() {
    }

    public static CarreraEntity aEntidad(CarreraRequestDTO request) {
        return CarreraEntity.builder()
                .codigo(normalizarCodigo(request.getCodigo()))
                .nombre(request.getNombre().trim())
                .activo(true)
                .build();
    }

    public static void actualizarEntidad(
            CarreraEntity carrera,
            CarreraRequestDTO request) {
        carrera.setCodigo(normalizarCodigo(request.getCodigo()));
        carrera.setNombre(request.getNombre().trim());
    }

    public static CarreraResponseDTO aResponseDTO(CarreraEntity carrera) {
        return CarreraResponseDTO.builder()
                .id(carrera.getId())
                .codigo(carrera.getCodigo())
                .nombre(carrera.getNombre())
                .activo(carrera.getActivo())
                .build();
    }

    private static String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase();
    }
}
