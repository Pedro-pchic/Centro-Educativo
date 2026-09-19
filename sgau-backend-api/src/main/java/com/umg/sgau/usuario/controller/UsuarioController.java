package com.umg.sgau.usuario.controller;

import com.umg.sgau.usuario.dto.UsuarioRequestDTO;
import com.umg.sgau.usuario.dto.UsuarioResponseDTO;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.UsuarioNoEncontradoException;
import com.umg.sgau.usuario.mapper.UsuarioMapper;
import com.umg.sgau.usuario.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	private final UsuarioService usuarioService;
	
	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
		}
	
	@PostMapping
	public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
	UsuarioEntity usuarioCreado = usuarioService.crear(UsuarioMapper.aEntidad(request));
	UsuarioResponseDTO response = UsuarioMapper.aResponseDTO(usuarioCreado);
	return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
	    UsuarioEntity usuario = usuarioService.obtenerPorId(id);
	    return ResponseEntity.ok(UsuarioMapper.aResponseDTO(usuario));
	}
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	List<UsuarioEntity> usuarios = usuarioService.obtenerTodos();
	List<UsuarioResponseDTO> response = UsuarioMapper.aResponseDTOList(usuarios);
	return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> actualizar(
				@PathVariable Long id,
				@Valid @RequestBody UsuarioRequestDTO request) {
			UsuarioEntity usuarioActualizado = usuarioService.actualizar(
					id, UsuarioMapper.aEntidad(request));
			return ResponseEntity.ok(UsuarioMapper.aResponseDTO(usuarioActualizado));
			}
    
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
		}
		
}
	
	
	
	
	
