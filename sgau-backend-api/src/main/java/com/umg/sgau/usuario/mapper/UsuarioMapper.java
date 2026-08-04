package com.umg.sgau.usuario.mapper;

import java.util.List;
import java.util.stream.Collectors;


import com.umg.sgau.usuario.dto.UsuarioRequestDTO;
import com.umg.sgau.usuario.dto.UsuarioResponseDTO;
import com.umg.sgau.usuario.entity.UsuarioEntity;

public class UsuarioMapper {
	private UsuarioMapper() {
	}
	
	public static UsuarioEntity aEntidad(UsuarioRequestDTO dto) {
	UsuarioEntity usuario = new UsuarioEntity();
	usuario.setUsername(dto.getUsername());
	usuario.setPassword(dto.getPassword());
	usuario.setEmail(dto.getEmail());
	usuario.setNombre(dto.getNombre());
	usuario.setApellido(dto.getApellido());
	usuario.setActivo(dto.getActivo());
	return usuario;
	}
	public static UsuarioResponseDTO aResponseDTO(UsuarioEntity usuario) {
	UsuarioResponseDTO dto = new UsuarioResponseDTO();
	dto.setId(usuario.getId());
	dto.setUsername(usuario.getUsername());
	dto.setEmail(usuario.getEmail());
	dto.setNombre(usuario.getNombre());
	dto.setApellido(usuario.getApellido());
	dto.setActivo(usuario.getActivo());
	dto.setFechaCreacion(usuario.getFechaCreacion());
	return dto;
	}
	public static List<UsuarioResponseDTO> aResponseDTOList(List<UsuarioEntity>
	usuarios) {
	return usuarios.stream()
	.map(UsuarioMapper::aResponseDTO)
	.collect(Collectors.toList());
	}
}
