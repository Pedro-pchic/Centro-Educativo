package com.umg.sgau.colegiatura.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ColegiaturaRequestDTO {


    @NotBlank(message = "El mes es obligatorio")
    private String mes;


    @NotBlank(message = "El ciclo es obligatorio")
    private String ciclo;


    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(
        value = "0.01",
        message = "El monto debe ser mayor a cero"
    )
    private BigDecimal monto;


    private Boolean pagado;


    private LocalDateTime fechaPago;


    @NotNull(message = "El estudiante es obligatorio")
    private Long idEstudiante;

}