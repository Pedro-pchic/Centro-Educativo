package com.umg.sgau.nota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaResponseDTO {
    
    private Long id;
    
    // Relaciones devueltas como IDs para mantener la respuesta ligera
    private Long estudianteId;
    private String estudianteNombre;
    private Long cursoId;
    private String cursoNombre;
    private Long inscripcionId;
    
    private String cicloAcademico;
    private BigDecimal zona;
    private BigDecimal examenFinal;
    private BigDecimal notaFinal;
    private String estado;
    private Boolean activo;
    private LocalDate fechaRegistro;
}