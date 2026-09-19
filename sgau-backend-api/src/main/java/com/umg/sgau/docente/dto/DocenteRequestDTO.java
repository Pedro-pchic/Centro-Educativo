package com.umg.sgau.docente.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocenteRequestDTO {
	
	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 50, message = "El nombre no puede superar 50 caracteres")
	private String nombre;
	@NotBlank(message = "El apellido es obligatorio")
	@Size(max = 50, message = "El apellido no puede superar 50 caracteres")
	private String apellido;
	@NotBlank(message = "El DPI es obligatorio")
	@Size(max = 13, message = "El DPI no puede superar 13 caracteres")
	private String dpi;
	@NotBlank(message = "El correo institucional es obligatorio")
	@Email(message = "El correo institucional debe tener un formato válido")
	@Size(max = 100, message = "El correo institucional no puede superar 100 caracteres")
	private String emailInstitucional;
	@NotBlank(message = "El correo personal es obligatorio")
	@Email(message = "El correo personal debe tener un formato válido")
	@Size(max = 100, message = "El correo personal no puede superar 100 caracteres")
	private String emailPersonal;
	@Size(max = 15, message = "El teléfono no puede superar 15 caracteres")
	private String telefono;
	@Size(max = 100, message = "La especialidad no puede superar 100 caracteres")
	private String especialidad;
	@NotNull(message = "La fecha de contratación es obligatoria")
	private LocalDate fechaContratacion;
	

	
}
