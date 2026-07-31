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
	private String emailpersonal;
	
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
	
	public DocenteEntity() {
	}
	
	@PrePersist
	protected void alPersistir() {
		this.fechaCreacion = LocalDateTime.now();
		if (this.activo == null) {
			this.activo = true;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmailInstitucional() {
		return emailInstitucional;
	}

	public void setEmailInstitucional(String emailInstitucional) {
		this.emailInstitucional = emailInstitucional;
	}

	public String getEmailPersonal() {
		return emailpersonal;
	}

	public void setEmailPersonal(String emailpersonal) {
		this.emailpersonal = emailpersonal;
	}

	public String getDpi() {
		return dpi;
	}

	public void setDpi(String dpi) {
		this.dpi = dpi;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public LocalDate getFechaContratacion() {
		return fechaContratacion;
	}

	public void setFechaContratacion(LocalDate fechaContratacion) {
		this.fechaContratacion = fechaContratacion;
	}

	public Boolean getEstado() {
		return activo;
	}

	public void setEstado(Boolean estado) {
		this.activo = estado;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	
	
}