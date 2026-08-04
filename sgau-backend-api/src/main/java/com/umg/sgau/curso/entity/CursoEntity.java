package com.umg.sgau.curso.entity;

import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.docente.entity.DocenteEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "codigo",
        nullable = false,
        unique = true,
        length = 20
    )
    private String codigo;

    @Column(
        name = "nombre",
        nullable = false,
        length = 150
    )
    private String nombre;

    @Column(
        name = "creditos",
        nullable = false
    )
    private Integer creditos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "carrera_id",
        nullable = false
    )
    private CarreraEntity carrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private DocenteEntity docente;
    
    @Builder.Default
    private Boolean activo = true;
}

