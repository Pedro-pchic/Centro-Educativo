package com.umg.sgau.nota.entity;

import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.inscripcion.entity.InscripcionEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ciclo_academico", length = 30, nullable = false)
    private String cicloAcademico;

    @Column(precision = 5, scale = 2)
    private BigDecimal zona; 

    @Column(precision = 5, scale = 2)
    private BigDecimal examenFinal;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal notaFinal;

    @Column(length = 20)
    private String estado;
    
    @Column(nullable = false)
	private Boolean activo;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

    @PrePersist
    public void alPersistir() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.activo == null) {
            this.activo = true;
        }

        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }

        calcularNotaFinal();
    }

    @PreUpdate
    public void alActualizar() {
        calcularNotaFinal();
    }

    private void calcularNotaFinal() {
        BigDecimal zonaPuntos =
                this.zona != null ? this.zona : BigDecimal.ZERO;
        BigDecimal examenPuntos =
                this.examenFinal != null ? this.examenFinal : BigDecimal.ZERO;

        this.notaFinal = zonaPuntos.add(examenPuntos);
    }
    
    // 3. Relación con Inscripcion (Muchas notas -> Una Inscripción)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private InscripcionEntity inscripcion;
}
