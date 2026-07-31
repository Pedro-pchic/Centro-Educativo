package com.umg.sgau.estudiante.controller;

import com.umg.sgau.estudiante.dto.EstudianteRequestDTO;
import com.umg.sgau.estudiante.dto.EstudianteResponseDTO;
import com.umg.sgau.estudiante.service.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstudianteResponseDTO crear(@RequestBody EstudianteRequestDTO request) {
        return estudianteService.crear(request);
    }

    @GetMapping
    public List<EstudianteResponseDTO> listar() {
        return estudianteService.listar();
    }

    @GetMapping("/{id}")
    public EstudianteResponseDTO buscarPorId(@PathVariable Long id) {
        return estudianteService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public EstudianteResponseDTO actualizar(@PathVariable Long id,
                                            @RequestBody EstudianteRequestDTO request) {
        return estudianteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        estudianteService.eliminar(id);
    }
}
