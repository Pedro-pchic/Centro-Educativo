package com.umg.sgau.usuario.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter 
public class UsuarioRequestDTO {
	private String username;
	private String password;
	private String email;
	private String nombre;
	private String apellido;
	private Boolean activo;
	
}
