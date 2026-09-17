package com.umg.sgau.usuario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsociarEstudianteRequestDTO {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long estudianteId;
}
