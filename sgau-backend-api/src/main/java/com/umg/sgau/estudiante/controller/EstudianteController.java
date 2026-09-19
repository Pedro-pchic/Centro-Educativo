package com.umg.sgau.estudiante.controller;

import com.umg.sgau.estudiante.exception.*;
import com.umg.sgau.estudiante.dto.EstudianteRequestDTO;
import com.umg.sgau.estudiante.dto.EstudianteResponseDTO;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.mapper.EstudianteMapper;
import com.umg.sgau.estudiante.service.EstudianteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crear(
            @Valid @RequestBody EstudianteRequestDTO request) {

        EstudianteEntity estudianteCreado =
                estudianteService.crear(
                        EstudianteMapper.aEntidad(request)
                );

        EstudianteResponseDTO response =
                EstudianteMapper.aResponseDTO(estudianteCreado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerPorId(
            @PathVariable Long id) {
        EstudianteEntity estudiante = estudianteService.obtenerPorId(id);
        return ResponseEntity.ok(EstudianteMapper.aResponseDTO(estudiante));
    }


    @GetMapping
    public ResponseEntity<?> obtenerTodos() {

        List<EstudianteEntity> estudiantes =
                estudianteService.obtenerTodos();

        List<EstudianteResponseDTO> response =
                EstudianteMapper.aResponseDTOList(estudiantes);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstudianteRequestDTO request) {
        EstudianteEntity estudianteActualizado = estudianteService.actualizar(
                id, EstudianteMapper.aEntidad(request));
        return ResponseEntity.ok(EstudianteMapper.aResponseDTO(estudianteActualizado));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {
        estudianteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
