package com.umg.sgau.nota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaRequestDTO {
    
    // Identificadores de relaciones
    @NotNull(message = "La inscripción es obligatoria")
    @Positive(message = "El identificador de la inscripción debe ser mayor que cero")
    private Long inscripcionId;
    
    // Datos propios de Notas
    @NotBlank(message = "El ciclo académico es obligatorio")
    @Size(max = 30, message = "El ciclo académico no puede superar 30 caracteres")
    private String cicloAcademico;
    private BigDecimal zona;
    private BigDecimal examenFinal;
    @Size(max = 20, message = "El estado no puede superar 20 caracteres")
    private String estado;
    private LocalDate fechaRegistro;
}
