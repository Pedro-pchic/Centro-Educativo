package com.umg.sgau.curso.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.umg.sgau.curso.dto.CursoRequestDTO;
import com.umg.sgau.curso.dto.CursoResponseDTO;
import com.umg.sgau.curso.service.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    public ResponseEntity<CursoResponseDTO> crear(
            @Valid @RequestBody CursoRequestDTO request) {

        CursoResponseDTO curso = cursoService.crear(request);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(curso.getId())
                .toUri();

        return ResponseEntity
                .created(ubicacion)
                .body(curso);
    }

    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>>
            obtenerTodos() {

        return ResponseEntity.ok(
                cursoService.obtenerTodos()
        );
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<CursoResponseDTO>>
            obtenerInactivos() {

        return ResponseEntity.ok(
                cursoService.obtenerInactivos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cursoService.obtenerPorId(id)
        );
    }

    @GetMapping("/carrera/{carreraId}")
    public ResponseEntity<List<CursoResponseDTO>>
            obtenerPorCarrera(
                    @PathVariable Long carreraId) {

        return ResponseEntity.ok(
                cursoService.obtenerPorCarrera(carreraId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequestDTO request) {

        return ResponseEntity.ok(
                cursoService.actualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        cursoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restaurar")
    public ResponseEntity<CursoResponseDTO> restaurar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cursoService.restaurar(id)
        );
    }
}

