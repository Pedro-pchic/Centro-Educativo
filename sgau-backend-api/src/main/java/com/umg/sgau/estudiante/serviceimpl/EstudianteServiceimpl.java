package com.umg.sgau.estudiante.serviceimpl;

import com.umg.sgau.estudiante.exception.*;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.estudiante.service.EstudianteService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstudianteServiceimpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    public EstudianteServiceimpl(
            EstudianteRepository estudianteRepository) {

        this.estudianteRepository = estudianteRepository;
    }

    @Override
    public EstudianteEntity crear(EstudianteEntity estudiante) {
    	estudiante.setActivo(true);

        return estudianteRepository.save(estudiante);
    }

    @Override
    public EstudianteEntity obtenerPorId(Long id) {

        Optional<EstudianteEntity> estudianteEncontrado =
                estudianteRepository.findById(id);

        if (estudianteEncontrado.isEmpty()) {
            throw new EstudianteNoEncontradoException(id);
        }

        EstudianteEntity estudiante =
                estudianteEncontrado.get();

        if (!Boolean.TRUE.equals(estudiante.getActivo())) {
            throw new EstudianteNoEncontradoException(id);
        }

        return estudiante;
    }

    @Override
    public List<EstudianteEntity> obtenerTodos() {

        return estudianteRepository.findAll()
                .stream()
                .filter(estudiante ->
                        Boolean.TRUE.equals(estudiante.getActivo()))
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteEntity actualizar(
            Long id,
            EstudianteEntity estudiante) {

        Optional<EstudianteEntity> estudianteExistente =
                estudianteRepository.findById(id);

        if (estudianteExistente.isEmpty()) {
            throw new EstudianteNoEncontradoException(id);
        }

        EstudianteEntity estudianteActual =
                estudianteExistente.get();

        if (!Boolean.TRUE.equals(estudianteActual.getActivo())) {
            throw new EstudianteNoEncontradoException(id);
        }

        estudianteActual.setNombre(estudiante.getNombre());
        estudianteActual.setApellido(estudiante.getApellido());
        estudianteActual.setEmail(estudiante.getEmail());
        estudianteActual.setCodigoEstudiante(
                estudiante.getCodigoEstudiante()
        );

        return estudianteRepository.save(estudianteActual);
    }

    @Override
    public void eliminar(Long id) {

        Optional<EstudianteEntity> estudianteExistente =
                estudianteRepository.findById(id);

        if (estudianteExistente.isEmpty()) {
            throw new EstudianteNoEncontradoException(id);
        }

        EstudianteEntity estudiante =
                estudianteExistente.get();

        estudiante.setActivo(false);

        estudianteRepository.save(estudiante);
    }

}