package com.umg.sgau.curso.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursoRequestDTO {

    @NotBlank(message = "El código del curso es obligatorio")
    @Size(
        max = 20,
        message = "El código no puede superar los 20 caracteres"
    )
    private String codigo;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(
        max = 150,
        message = "El nombre no puede superar los 150 caracteres"
    )
    private String nombre;

    @NotNull(message = "La cantidad de créditos es obligatoria")
    @Min(
        value = 1,
        message = "El curso debe tener al menos 1 crédito"
    )
    @Max(
        value = 20,
        message = "El curso no puede tener más de 20 créditos"
    )
    private Integer creditos;
    
    @NotNull(message = "El docente es obligatorio")
    @Positive(message = "El identificador del docente debe ser mayor que cero")
    private Long docenteId;

    @NotNull(message = "La carrera es obligatoria")
    private Long carreraId;
}

