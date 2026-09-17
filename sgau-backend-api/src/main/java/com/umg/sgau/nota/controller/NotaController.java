package com.umg.sgau.nota.controller;

import com.umg.sgau.nota.dto.NotaRequestDTO;
import com.umg.sgau.nota.dto.NotaResponseDTO;
import com.umg.sgau.nota.service.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @PostMapping
    public ResponseEntity<?> registrarNota(@RequestBody NotaRequestDTO requestDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(notaService.registrarNota(requestDTO));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotaResponseDTO> actualizarNota(
            @PathVariable Long id,
            @RequestBody NotaRequestDTO requestDTO) {
        return ResponseEntity.ok(notaService.actualizarNota(id, requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<NotaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(notaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.obtenerPorId(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerPorEstudiante(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(notaService.obtenerPorEstudiante(estudianteId));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerPorCurso(
            @PathVariable Long cursoId) {
        return ResponseEntity.ok(notaService.obtenerPorCurso(cursoId));
    }

    @GetMapping("/inscripcion/{inscripcionId}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerPorInscripcion(
            @PathVariable Long inscripcionId) {
        return ResponseEntity.ok(notaService.obtenerPorInscripcion(inscripcionId));
    }

    @GetMapping("/estudiante/{estudianteId}/curso/{cursoId}")
    public ResponseEntity<NotaResponseDTO> obtenerPorEstudianteYCurso(
            @PathVariable Long estudianteId,
            @PathVariable Long cursoId) {
        return ResponseEntity.ok(
                notaService.obtenerPorEstudianteYCurso(estudianteId, cursoId));
    }

    @GetMapping("/estudiante/{estudianteId}/promedio")
    public ResponseEntity<Double> obtenerPromedioEstudiante(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(notaService.calcularPromedioEstudiante(estudianteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNota(@PathVariable Long id) {
        notaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
