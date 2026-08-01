package com.umg.sgau.estudiante.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstudianteRequestDTO {

    private String codigoEstudiante;
    private String email;
    private String nombre;
    private String apellido;
    private Boolean activo;

}
