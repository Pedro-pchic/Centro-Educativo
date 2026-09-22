package com.umg.sgau.usuario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsociarDocenteRequestDTO {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long docenteId;
}
