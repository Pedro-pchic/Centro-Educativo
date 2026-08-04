package com.umg.sgau.carrera.controller;

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

import com.umg.sgau.carrera.dto.CarreraRequestDTO;
import com.umg.sgau.carrera.dto.CarreraResponseDTO;
import com.umg.sgau.carrera.service.CarreraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @PostMapping
    public ResponseEntity<CarreraResponseDTO> crear(
            @Valid @RequestBody CarreraRequestDTO request) {
        CarreraResponseDTO carrera = carreraService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(carrera.getId())
                .toUri();
        return ResponseEntity.created(ubicacion).body(carrera);
    }

    @GetMapping
    public ResponseEntity<List<CarreraResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(carreraService.obtenerTodos());
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<CarreraResponseDTO>> obtenerInactivos() {
        return ResponseEntity.ok(carreraService.obtenerInactivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CarreraRequestDTO request) {
        return ResponseEntity.ok(carreraService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restaurar")
    public ResponseEntity<CarreraResponseDTO> restaurar(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.restaurar(id));
    }
}
