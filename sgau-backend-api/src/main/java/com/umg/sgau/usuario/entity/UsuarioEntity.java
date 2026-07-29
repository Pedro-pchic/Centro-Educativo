package com.umg.sgau.usuario.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 50)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false, unique = true, length = 100)
	private String email;
	@Column(nullable = false, length = 60)
	private String nombre;
	@Column(nullable = false, length = 60)
	private String apellido;
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
