package com.umg.sgau.auth.dto;

import com.umg.sgau.usuario.entity.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String type;
    private String username;
    private RolUsuario rol;
}
