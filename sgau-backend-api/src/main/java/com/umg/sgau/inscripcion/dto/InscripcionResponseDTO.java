package com.umg.sgau.inscripcion.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InscripcionResponseDTO {

    private Long id;
    private Long estudianteId;
    private String codigoEstudiante;
    private String nombreEstudiante;
    private Long cursoId;
    private String nombreCurso;
    private LocalDate fechaInscripcion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
