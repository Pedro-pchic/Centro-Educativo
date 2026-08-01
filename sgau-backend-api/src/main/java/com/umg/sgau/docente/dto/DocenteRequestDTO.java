package com.umg.sgau.docente.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocenteRequestDTO {
	
	private String nombre;
	private String apellido;
	private String dpi;
	private String emailInstitucional;
	private String emailPersonal;
	private String telefono;
	private String especialidad;
	private LocalDate fechaContratacion;
	

	
}
