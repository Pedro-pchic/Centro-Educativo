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

import java.util.List;
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	private final UsuarioService usuarioService;
	
	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
		}
	
	@PostMapping
	public ResponseEntity<?> crear(@RequestBody UsuarioRequestDTO request) {
	UsuarioEntity usuarioCreado = usuarioService.crear(UsuarioMapper.aEntidad(request));
	UsuarioResponseDTO response = UsuarioMapper.aResponseDTO(usuarioCreado);
	return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
	    try {
	        UsuarioEntity usuario = usuarioService.obtenerPorId(id);

	        return ResponseEntity.ok(
	                UsuarioMapper.aResponseDTO(usuario)
	        );

	    } catch (UsuarioNoEncontradoException ex) {
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(ex.getMessage());
	    }
	}
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	List<UsuarioEntity> usuarios = usuarioService.obtenerTodos();
	List<UsuarioResponseDTO> response = UsuarioMapper.aResponseDTOList(usuarios);
	return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO request) {
			try {
				UsuarioEntity usuarioActualizado = usuarioService.actualizar(id, UsuarioMapper.aEntidad(request));
				return ResponseEntity.ok(UsuarioMapper.aResponseDTO(usuarioActualizado));
				} 		catch (UsuarioNoEncontradoException ex) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
			}
			}
    
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
	try {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
		} catch (UsuarioNoEncontradoException ex) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
				}
			}
		
}
	
	
	
	
	

