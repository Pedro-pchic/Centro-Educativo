package com.umg.sgau.nota.controller;

import com.umg.sgau.nota.dto.NotaRequestDTO;
import com.umg.sgau.nota.dto.NotaResponseDTO;
import com.umg.sgau.nota.exception.NotaNoEncontradaException;
import com.umg.sgau.nota.service.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService notaService;
    
    public NotaController(NotaService notaService) { 
        this.notaService = notaService; 
    }

    // Registrar calificaciones
    @PostMapping
    public ResponseEntity<?> registrarNota(@RequestBody NotaRequestDTO requestDTO) {
        try {
        	
            NotaResponseDTO notaGuardada = notaService.registrarNota(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(notaGuardada);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la nota: " + ex.getMessage());
        }
    }

    // Actualizar calificaciones
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarNota(@PathVariable Long id, @RequestBody NotaRequestDTO requestDTO) {
        try {
            NotaResponseDTO notaActualizada = notaService.actualizarNota(id, requestDTO);
            return ResponseEntity.ok(notaActualizada);
        } catch (NotaNoEncontradaException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la nota: " + ex.getMessage());
        }
    }

    // Consultar notas por diferentes criterios
    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        try {
            List<NotaResponseDTO> notas = notaService.obtenerTodas();
            return ResponseEntity.ok(notas);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notaService.obtenerPorId(id));
        } catch (NotaNoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<?> obtenerPorEstudiante(@PathVariable Long estudianteId) {
        try {
            List<NotaResponseDTO> notas = (List<NotaResponseDTO>) notaService.obtenerPorEstudiante(estudianteId);
            return ResponseEntity.ok(notas);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<?> obtenerPorCurso(@PathVariable Long cursoId) {
        try {
            List<NotaResponseDTO> notas = notaService.obtenerPorCurso(cursoId);
            return ResponseEntity.ok(notas);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/inscripcion/{inscripcionId}")
    public ResponseEntity<?> obtenerPorInscripcion(@PathVariable Long inscripcionId) {
        try {
            List<NotaResponseDTO> notas = notaService.obtenerPorInscripcion(inscripcionId);
            return ResponseEntity.ok(notas);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/estudiante/{estudianteId}/curso/{cursoId}")
    public ResponseEntity<?> obtenerPorEstudianteYCurso(@PathVariable Long estudianteId, @PathVariable Long cursoId) {
        try {
        	NotaResponseDTO nota = notaService.obtenerPorEstudianteYCurso(estudianteId, cursoId);
            return ResponseEntity.ok(nota);
        } catch (NotaNoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Calcular automáticamente el promedio
    @GetMapping("/estudiante/{estudianteId}/promedio")
    public ResponseEntity<?> obtenerPromedioEstudiante(@PathVariable Long estudianteId) {
        try {
            Double promedio = notaService.calcularPromedioEstudiante(estudianteId);
            return ResponseEntity.ok(promedio);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNota(@PathVariable Long id) {
        try {
            notaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (NotaNoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }
}
