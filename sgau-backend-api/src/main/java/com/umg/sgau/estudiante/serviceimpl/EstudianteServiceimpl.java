package com.umg.sgau.estudiante.serviceimpl;

import com.umg.sgau.estudiante.exception.*;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.estudiante.service.EstudianteService;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.service.IdentidadAcademicaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstudianteServiceimpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final IdentidadAcademicaService identidadAcademicaService;

    @Autowired
    public EstudianteServiceimpl(
            EstudianteRepository estudianteRepository,
            IdentidadAcademicaService identidadAcademicaService) {

        this.estudianteRepository = estudianteRepository;
        this.identidadAcademicaService = identidadAcademicaService;
    }

    public EstudianteServiceimpl(EstudianteRepository estudianteRepository) {
        this(estudianteRepository, null);
    }

    @Override
    public EstudianteEntity crear(EstudianteEntity estudiante) {
        requerirAdmin();
        estudiante.setActivo(true);

        return estudianteRepository.save(estudiante);
    }

    @Override
    public EstudianteEntity obtenerPorId(Long id) {

        validarAccesoEstudiante(id);

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

        requerirAdmin();

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

        requerirAdmin();

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
        estudianteActual.setCarnet(estudiante.getCarnet());

        return estudianteRepository.save(estudianteActual);
    }

    @Override
    public void eliminar(Long id) {

        requerirAdmin();

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

    @Override
    public EstudianteEntity obtenerAutenticado() {
        requerirIdentidad();
        return identidadAcademicaService.obtenerEstudianteAutenticado();
    }

    private void requerirAdmin() {
        if (identidadAcademicaService != null
                && !identidadAcademicaService.esAdmin()) {
            throw new AsociacionAcademicaException(
                    "Solo ADMIN puede administrar estudiantes.");
        }
    }

    private void validarAccesoEstudiante(Long id) {
        if (identidadAcademicaService == null) {
            return;
        }

        UsuarioEntity usuario = identidadAcademicaService.obtenerUsuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (usuario.getRol() == RolUsuario.ESTUDIANTE
                && identidadAcademicaService.obtenerEstudianteAutenticado()
                        .getId().equals(id)) {
            return;
        }
        throw new AsociacionAcademicaException(
                "No tiene permisos para consultar este estudiante.");
    }

    private void requerirIdentidad() {
        if (identidadAcademicaService == null) {
            throw new AsociacionAcademicaException(
                    "No se configuró el resolver de identidad académica.");
        }
    }

}
