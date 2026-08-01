package com.umg.sgau.estudiante.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstudianteResponseDTO {

    private Long id;
    private String codigoEstudiante;
    private String email;
    private String nombre;
    private String apellido;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

}
