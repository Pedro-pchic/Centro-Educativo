package com.umg.sgau.curso.mapper;

import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.curso.dto.CursoRequestDTO;
import com.umg.sgau.curso.dto.CursoResponseDTO;
import com.umg.sgau.curso.entity.CursoEntity;

public final class CursoMapper {

    private CursoMapper() {
    }

    public static CursoEntity aEntidad(
            CursoRequestDTO request,
            CarreraEntity carrera) {

        return CursoEntity.builder()
                .codigo(normalizarCodigo(request.getCodigo()))
                .nombre(request.getNombre().trim())
                .creditos(request.getCreditos())
                .carrera(carrera)
                .activo(true)
                .build();
    }

    public static void actualizarEntidad(
            CursoEntity curso,
            CursoRequestDTO request,
            CarreraEntity carrera) {

        curso.setCodigo(normalizarCodigo(request.getCodigo()));
        curso.setNombre(request.getNombre().trim());
        curso.setCreditos(request.getCreditos());
        curso.setCarrera(carrera);
    }

    public static CursoResponseDTO aResponseDTO(CursoEntity curso) {
        return CursoResponseDTO.builder()
                .id(curso.getId())
                .codigo(curso.getCodigo())
                .nombre(curso.getNombre())
                .creditos(curso.getCreditos())
                .carreraId(curso.getCarrera().getId())
                .carreraCodigo(curso.getCarrera().getCodigo())
                .carreraNombre(curso.getCarrera().getNombre())
                .activo(curso.getActivo())
                .build();
    }

    private static String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase();
    }
}

