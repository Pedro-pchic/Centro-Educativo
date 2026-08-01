package com.umg.sgau.colegiatura.entity;

import com.umg.sgau.estudiante.entity.EstudianteEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "colegiaturas")
public class ColegiaturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String mes;

    @Column(nullable = false, length = 20)
    private String ciclo;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private Boolean pagado;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_estudiante",
            nullable = false
    )
    private EstudianteEntity estudiante;


    @PrePersist
    protected void alPersistir() {

        this.fechaCreacion = LocalDateTime.now();

        if (this.pagado == null) {
            this.pagado = false;
        }

        if (this.activo == null) {
            this.activo = true;
        }
    }
}
