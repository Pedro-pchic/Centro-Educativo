package com.umg.sgau.colegiatura.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ColegiaturaRequestDTO {

    private String mes;

    private String ciclo;

    private Double monto;

    private Boolean pagado;

    private LocalDateTime fechaPago;

    private Long idEstudiante;

}