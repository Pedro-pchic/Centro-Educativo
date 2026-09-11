package com.umg.sgau.usuario.dto;

import com.umg.sgau.usuario.entity.RolUsuario;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter 
public class UsuarioResponseDTO {
	private Long id;
	private String username;
	private String email;
	private String nombre;
	private String apellido;
	private Boolean activo;
	private RolUsuario rol;
	private LocalDateTime fechaCreacion;
	
	
}
