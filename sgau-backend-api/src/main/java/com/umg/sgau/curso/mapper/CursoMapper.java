package com.umg.sgau.curso.mapper;

import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.curso.dto.CursoRequestDTO;
import com.umg.sgau.curso.dto.CursoResponseDTO;
import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.docente.entity.DocenteEntity;

public final class CursoMapper {

    private CursoMapper() {
    }

    public static CursoEntity aEntidad(
            CursoRequestDTO request,
            CarreraEntity carrera,
    		DocenteEntity docente){

        return CursoEntity.builder()
                .codigo(normalizarCodigo(request.getCodigo()))
                .nombre(request.getNombre().trim())
                .creditos(request.getCreditos())
                .carrera(carrera)
                .docente(docente)
                .activo(true)
                .build();
    }

    public static void actualizarEntidad(
            CursoEntity curso,
            CursoRequestDTO request,
            CarreraEntity carrera,
            DocenteEntity docente) {

        curso.setCodigo(normalizarCodigo(request.getCodigo()));
        curso.setNombre(request.getNombre().trim());
        curso.setCreditos(request.getCreditos());
        curso.setCarrera(carrera);
        curso.setDocente(docente);
    }

    public static CursoResponseDTO aResponseDTO(CursoEntity curso) {

        CursoResponseDTO.CursoResponseDTOBuilder builder =
                CursoResponseDTO.builder()
                        .id(curso.getId())
                        .codigo(curso.getCodigo())
                        .nombre(curso.getNombre())
                        .creditos(curso.getCreditos())
                        .carreraId(curso.getCarrera().getId())
                        .carreraCodigo(curso.getCarrera().getCodigo())
                        .carreraNombre(curso.getCarrera().getNombre())
                        .activo(curso.getActivo());

        if (curso.getDocente() != null) {
            builder.docenteId(curso.getDocente().getId());

            builder.docenteNombre(
                    construirNombreDocente(curso.getDocente())
            );
        }

        return builder.build();
    }

    private static String construirNombreDocente(
            DocenteEntity docente) {

        String nombre = docente.getNombre() == null
                ? ""
                : docente.getNombre().trim();

        String apellido = docente.getApellido() == null
                ? ""
                : docente.getApellido().trim();

        return (nombre + " " + apellido).trim();
    }

    private static String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase();
    }
}

