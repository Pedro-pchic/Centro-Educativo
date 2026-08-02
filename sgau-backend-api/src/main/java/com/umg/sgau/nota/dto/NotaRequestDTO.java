package com.umg.sgau.nota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaRequestDTO {
    
    // Identificadores de relaciones
    private Long inscripcionId;
    
    // Datos propios de Notas
    private String cicloAcademico;
    private BigDecimal zona;
    private BigDecimal examenFinal;
    private String estado;
    private LocalDate fechaRegistro;
}
