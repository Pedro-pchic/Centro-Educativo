package com.umg.sgau.estudiante.controller;

import com.umg.sgau.exception.EstudianteNoEncontradoException;
import com.umg.sgau.estudiante.dto.EstudianteRequestDTO;
import com.umg.sgau.estudiante.dto.EstudianteResponseDTO;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.mapper.EstudianteMapper;
import com.umg.sgau.estudiante.service.EstudianteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody EstudianteRequestDTO request) {

        EstudianteEntity estudianteCreado =
                estudianteService.crear(
                        EstudianteMapper.aEntidad(request)
                );

        EstudianteResponseDTO response =
                EstudianteMapper.aResponseDTO(estudianteCreado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Long id) {

        try {

            EstudianteEntity estudiante =
                    estudianteService.obtenerPorId(id);

            return ResponseEntity.ok(
                    EstudianteMapper.aResponseDTO(estudiante)
            );

        } catch (EstudianteNoEncontradoException ex) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
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
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody EstudianteRequestDTO request) {

        try {

            EstudianteEntity estudianteActualizado =
                    estudianteService.actualizar(
                            id,
                            EstudianteMapper.aEntidad(request)
                    );

            return ResponseEntity.ok(
                    EstudianteMapper.aResponseDTO(
                            estudianteActualizado
                    )
            );

        } catch (EstudianteNoEncontradoException ex) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id) {

        try {

            estudianteService.eliminar(id);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (EstudianteNoEncontradoException ex) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }
}
