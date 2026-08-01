package com.umg.sgau.colegiatura.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ColegiaturaResponseDTO {

    private Long id;

    private String mes;

    private String ciclo;

    private Double monto;

    private Boolean pagado;

    private Boolean activo;

    private LocalDateTime fechaPago;

    private LocalDateTime fechaCreacion;

    private Long idEstudiante;

}