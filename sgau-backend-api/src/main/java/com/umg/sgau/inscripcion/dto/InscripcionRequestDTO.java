package com.umg.sgau.inscripcion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InscripcionRequestDTO {

    @NotNull(message = "El estudiante es obligatorio")
    @Positive(message = "El identificador del estudiante debe ser mayor que cero")
    private Long estudianteId;

    @NotNull(message = "El curso es obligatorio")
    @Positive(message = "El identificador del curso debe ser mayor que cero")
    private Long cursoId;

    private LocalDate fechaInscripcion;
}
