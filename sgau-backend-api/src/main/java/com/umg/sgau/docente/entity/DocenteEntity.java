package com.umg.sgau.docente.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(
			name = "usuario_id",
			foreignKey = @ForeignKey(name = "fk_docente_usuario")
	)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private UsuarioEntity usuario;
	
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
