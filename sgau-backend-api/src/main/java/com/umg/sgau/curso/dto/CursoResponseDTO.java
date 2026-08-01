package com.umg.sgau.curso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoResponseDTO {

    private Long id;

    private String codigo;

    private String nombre;

    private Integer creditos;

    private Long carreraId;

    private String carreraCodigo;

    private String carreraNombre;

    private Boolean activo;
}

