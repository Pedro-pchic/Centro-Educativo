package com.umg.sgau.docente.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.umg.sgau.docente.dto.DocenteRequestDTO;
import com.umg.sgau.docente.dto.DocenteResponseDTO;
import com.umg.sgau.docente.entity.DocenteEntity;

public class DocenteMapper {
	
	private DocenteMapper() {
		
	}
	
	public static DocenteEntity aEntidad(DocenteRequestDTO dto) {
		DocenteEntity docente = new DocenteEntity();
		docente.setNombre(dto.getNombre());
		docente.setApellido(dto.getApellido());
		docente.setDpi(dto.getDpi());
		docente.setEmailInstitucional(dto.getEmailInstitucional());
		docente.setEmailPersonal(dto.getEmailPersonal());
		docente.setTelefono(dto.getTelefono());
		docente.setEspecialidad(dto.getEspecialidad());
		docente.setFechaContratacion(dto.getFechaContratacion());
		
		return docente;
	}
	
	public static DocenteResponseDTO aResponseDTO(DocenteEntity docente) {
		DocenteResponseDTO dto = new DocenteResponseDTO();
		dto.setId(docente.getId());
		dto.setNombre(docente.getNombre());
		dto.setApellido(docente.getApellido());
		dto.setDpi(docente.getDpi());
		dto.setDpi(docente.getDpi());
		dto.setEmailInstitucional(docente.getEmailInstitucional());
		dto.setEmailPersonal(docente.getEmailPersonal());
		dto.setTelefono(docente.getTelefono());
		dto.setEspecialidad(docente.getEspecialidad());
		dto.setFechaContratacion(docente.getFechaContratacion());
		dto.setEstado(docente.getEstado());
		
		return dto;
	}
	
	public static List<DocenteResponseDTO> aResponseDTOList(List<DocenteEntity> docente){
		return docente.stream()
				.map(DocenteMapper::aResponseDTO)
				.collect(Collectors.toList());
	}
}
