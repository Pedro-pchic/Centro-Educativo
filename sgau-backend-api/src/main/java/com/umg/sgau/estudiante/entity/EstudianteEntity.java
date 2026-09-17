package com.umg.sgau.estudiante.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "estudiantes")
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String apellido;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String codigoEstudiante;
    

    @Column(nullable = false, unique = true, length = 20)
    private String carnet;

    @Column(nullable = false)
    private Boolean activo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_estudiantes_usuario")
    )
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
