package com.umg.sgau.inscripcion.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.umg.sgau.inscripcion.dto.InscripcionRequestDTO;
import com.umg.sgau.inscripcion.dto.InscripcionResponseDTO;
import com.umg.sgau.inscripcion.service.InscripcionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> crear(
            @Valid @RequestBody InscripcionRequestDTO request) {
        InscripcionResponseDTO response = inscripcionService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(inscripcionService.obtenerTodas());
    }

    @GetMapping("/paginadas")
    public ResponseEntity<Page<InscripcionResponseDTO>> obtenerTodasPaginadas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(inscripcionService.obtenerTodasPaginadas(pagina, tamanio));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerPorEstudiante(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(inscripcionService.obtenerPorEstudiante(estudianteId));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerPorCurso(
            @PathVariable Long cursoId) {
        return ResponseEntity.ok(inscripcionService.obtenerPorCurso(cursoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InscripcionRequestDTO request) {
        return ResponseEntity.ok(inscripcionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inscripcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
