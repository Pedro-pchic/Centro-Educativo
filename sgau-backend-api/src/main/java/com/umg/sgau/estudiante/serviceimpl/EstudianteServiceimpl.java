package com.umg.sgau.estudiante.service.impl;

import com.umg.sgau.estudiante.dto.EstudianteRequestDTO;
import com.umg.sgau.estudiante.dto.EstudianteResponseDTO;
import com.umg.sgau.estudiante.entity.Estudiante;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.estudiante.service.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Override
    public EstudianteResponseDTO crear(EstudianteRequestDTO request) {

        Estudiante estudiante = new Estudiante();
        estudiante.setCarnet(request.getCarnet());
        estudiante.setNombre(request.getNombre());
        estudiante.setApellido(request.getApellido());
        estudiante.setEmail(request.getEmail());
        estudiante.setTelefono(request.getTelefono());
        estudiante.setDireccion(request.getDireccion());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());

        Estudiante guardado = estudianteRepository.save(estudiante);

        return convertirDTO(guardado);
    }

    @Override
    public List<EstudianteResponseDTO> listar() {
        return estudianteRepository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteResponseDTO buscarPorId(Long id) {

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        return convertirDTO(estudiante);
    }

    @Override
    public EstudianteResponseDTO actualizar(Long id, EstudianteRequestDTO request) {

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.setCarnet(request.getCarnet());
        estudiante.setNombre(request.getNombre());
        estudiante.setApellido(request.getApellido());
        estudiante.setEmail(request.getEmail());
        estudiante.setTelefono(request.getTelefono());
        estudiante.setDireccion(request.getDireccion());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());

        Estudiante actualizado = estudianteRepository.save(estudiante);

        return convertirDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudianteRepository.delete(estudiante);
    }

    private EstudianteResponseDTO convertirDTO(Estudiante estudiante) {

        EstudianteResponseDTO dto = new EstudianteResponseDTO();

        dto.setId(estudiante.getId());
        dto.setCarnet(estudiante.getCarnet());
        dto.setNombre(estudiante.getNombre());
        dto.setApellido(estudiante.getApellido());
        dto.setEmail(estudiante.getEmail());
        dto.setTelefono(estudiante.getTelefono());
        dto.setDireccion(estudiante.getDireccion());
        dto.setFechaNacimiento(estudiante.getFechaNacimiento());
        dto.setActivo(estudiante.getActivo());
        dto.setFechaCreacion(estudiante.getFechaCreacion());

        return dto;
    }
}
