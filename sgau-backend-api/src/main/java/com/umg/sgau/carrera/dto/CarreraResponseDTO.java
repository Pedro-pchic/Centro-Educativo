package com.umg.sgau.carrera.dto;

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
public class CarreraResponseDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private Boolean activo;
}
