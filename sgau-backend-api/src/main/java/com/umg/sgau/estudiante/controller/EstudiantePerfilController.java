package com.umg.sgau.estudiante.controller;

import com.umg.sgau.colegiatura.dto.ColegiaturaResponseDTO;
import com.umg.sgau.colegiatura.service.ColegiaturaService;
import com.umg.sgau.estudiante.dto.EstudianteResponseDTO;
import com.umg.sgau.estudiante.mapper.EstudianteMapper;
import com.umg.sgau.estudiante.service.EstudianteService;
import com.umg.sgau.inscripcion.dto.InscripcionResponseDTO;
import com.umg.sgau.inscripcion.service.InscripcionService;
import com.umg.sgau.nota.dto.NotaResponseDTO;
import com.umg.sgau.nota.service.NotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes/me")
public class EstudiantePerfilController {

    private final EstudianteService estudianteService;
    private final InscripcionService inscripcionService;
    private final NotaService notaService;
    private final ColegiaturaService colegiaturaService;

    public EstudiantePerfilController(
            EstudianteService estudianteService,
            InscripcionService inscripcionService,
            NotaService notaService,
            ColegiaturaService colegiaturaService) {
        this.estudianteService = estudianteService;
        this.inscripcionService = inscripcionService;
        this.notaService = notaService;
        this.colegiaturaService = colegiaturaService;
    }

    @GetMapping
    public ResponseEntity<EstudianteResponseDTO> obtenerPerfil() {
        return ResponseEntity.ok(
                EstudianteMapper.aResponseDTO(estudianteService.obtenerAutenticado()));
    }

    @GetMapping("/inscripciones")
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerInscripciones() {
        return ResponseEntity.ok(inscripcionService.obtenerMisInscripciones());
    }

    @GetMapping("/notas")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotas() {
        return ResponseEntity.ok(notaService.obtenerMisNotas());
    }

    @GetMapping("/colegiaturas")
    public ResponseEntity<List<ColegiaturaResponseDTO>> obtenerColegiaturas() {
        return ResponseEntity.ok(colegiaturaService.obtenerMisColegiaturas());
    }

    @GetMapping("/promedio")
    public ResponseEntity<Double> obtenerPromedio() {
        return ResponseEntity.ok(notaService.calcularMiPromedio());
    }
}
