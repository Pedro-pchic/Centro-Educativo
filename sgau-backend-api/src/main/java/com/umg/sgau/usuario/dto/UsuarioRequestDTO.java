package com.umg.sgau.usuario.dto;

import com.umg.sgau.usuario.entity.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter 
public class UsuarioRequestDTO {
	@NotBlank(message = "El username es obligatorio")
	@Size(max = 50, message = "El username no puede superar 50 caracteres")
	private String username;
	@NotBlank(message = "La contraseña es obligatoria")
	private String password;
	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "El correo debe tener un formato válido")
	@Size(max = 100, message = "El correo no puede superar 100 caracteres")
	private String email;
	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 60, message = "El nombre no puede superar 60 caracteres")
	private String nombre;
	@NotBlank(message = "El apellido es obligatorio")
	@Size(max = 60, message = "El apellido no puede superar 60 caracteres")
	private String apellido;
	private Boolean activo;
	private RolUsuario rol;
	
}
