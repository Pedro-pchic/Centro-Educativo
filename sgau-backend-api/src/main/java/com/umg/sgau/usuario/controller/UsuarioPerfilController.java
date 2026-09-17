package com.umg.sgau.usuario.controller;

import com.umg.sgau.docente.exception.DocenteNoEncontradoException;
import com.umg.sgau.estudiante.exception.EstudianteNoEncontradoException;
import com.umg.sgau.usuario.dto.AsociarDocenteRequestDTO;
import com.umg.sgau.usuario.dto.AsociarEstudianteRequestDTO;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.exception.UsuarioNoEncontradoException;
import com.umg.sgau.usuario.service.AsociacionAcademicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioPerfilController {

    private final AsociacionAcademicaService asociacionAcademicaService;

    public UsuarioPerfilController(AsociacionAcademicaService asociacionAcademicaService) {
        this.asociacionAcademicaService = asociacionAcademicaService;
    }

    @PostMapping("/asociar-estudiante")
    public ResponseEntity<?> asociarEstudiante(
            @Valid @RequestBody AsociarEstudianteRequestDTO request) {
        try {
            asociacionAcademicaService.asociarEstudiante(
                    request.getUsuarioId(),
                    request.getEstudianteId()
            );
            return ResponseEntity.noContent().build();
        } catch (UsuarioNoEncontradoException | EstudianteNoEncontradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AsociacionAcademicaException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/asociar-docente")
    public ResponseEntity<?> asociarDocente(
            @Valid @RequestBody AsociarDocenteRequestDTO request) {
        try {
            asociacionAcademicaService.asociarDocente(
                    request.getUsuarioId(),
                    request.getDocenteId()
            );
            return ResponseEntity.noContent().build();
        } catch (UsuarioNoEncontradoException | DocenteNoEncontradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AsociacionAcademicaException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
