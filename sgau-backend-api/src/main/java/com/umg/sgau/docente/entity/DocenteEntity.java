package com.umg.sgau.docente.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "docente")
public class DocenteEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 50)
	private String nombre;
	
	@Column(nullable = false, length = 50)
	private String apellido;
	
	@Column(name = "email_institucional", nullable = false, unique = true, length = 100)
	private String emailInstitucional;
	
	@Column(name = "email_personal", nullable = false, unique = true, length = 100)
	private String emailPersonal;
	
	@Column(nullable = false, unique = true, length = 13)
	private String dpi;
	
	@Column(length = 15)
	private String telefono;
	
	@Column(length = 100)
	private String especialidad; 
	
	@Column(name = "fecha_contratacion",nullable = false)
	private LocalDate fechaContratacion; 
	
	@Column(nullable = false)
	private Boolean activo;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;
	
	
	
	@PrePersist
	protected void alPersistir() {
		this.fechaCreacion = LocalDateTime.now();
		if (this.activo == null) {
			this.activo = true;
		}
	}
	
}