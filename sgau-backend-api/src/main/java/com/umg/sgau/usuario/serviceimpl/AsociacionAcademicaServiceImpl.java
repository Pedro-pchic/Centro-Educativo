package com.umg.sgau.usuario.serviceimpl;

import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.exception.DocenteNoEncontradoException;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.exception.EstudianteNoEncontradoException;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.exception.UsuarioNoEncontradoException;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import com.umg.sgau.usuario.service.AsociacionAcademicaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsociacionAcademicaServiceImpl implements AsociacionAcademicaService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;

    public AsociacionAcademicaServiceImpl(
            UsuarioRepository usuarioRepository,
            EstudianteRepository estudianteRepository,
            DocenteRepository docenteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    @Transactional
    public void asociarEstudiante(Long usuarioId, Long estudianteId) {
        UsuarioEntity usuario = obtenerUsuarioActivoConRol(
                usuarioId,
                RolUsuario.ESTUDIANTE
        );
        EstudianteEntity estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new EstudianteNoEncontradoException(estudianteId));

        validarPerfilActivo(estudiante.getActivo(), "estudiante", estudianteId);

        if (estudiante.getUsuario() != null) {
            throw new AsociacionAcademicaException(
                    "El estudiante ya está asociado a un usuario."
            );
        }
        if (estudianteRepository.existsByUsuarioId(usuarioId)) {
            throw new AsociacionAcademicaException(
                    "El usuario ya está asociado a otro estudiante."
            );
        }
        if (docenteRepository.existsByUsuarioId(usuarioId)) {
            throw new AsociacionAcademicaException(
                    "El usuario ya está asociado a un docente."
            );
        }

        estudiante.setUsuario(usuario);
        estudianteRepository.save(estudiante);
    }

    @Override
    @Transactional
    public void asociarDocente(Long usuarioId, Long docenteId) {
        UsuarioEntity usuario = obtenerUsuarioActivoConRol(
                usuarioId,
                RolUsuario.DOCENTE
        );
        DocenteEntity docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new DocenteNoEncontradoException(docenteId));

        validarPerfilActivo(docente.getActivo(), "docente", docenteId);

        if (docente.getUsuario() != null) {
            throw new AsociacionAcademicaException(
                    "El docente ya está asociado a un usuario."
            );
        }
        if (docenteRepository.existsByUsuarioId(usuarioId)) {
            throw new AsociacionAcademicaException(
                    "El usuario ya está asociado a otro docente."
            );
        }
        if (estudianteRepository.existsByUsuarioId(usuarioId)) {
            throw new AsociacionAcademicaException(
                    "El usuario ya está asociado a un estudiante."
            );
        }

        docente.setUsuario(usuario);
        docenteRepository.save(docente);
    }

    private UsuarioEntity obtenerUsuarioActivoConRol(
            Long usuarioId,
            RolUsuario rolRequerido) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }
        if (usuario.getRol() != rolRequerido) {
            throw new AsociacionAcademicaException(
                    "El usuario debe tener el rol " + rolRequerido.name() + "."
            );
        }

        return usuario;
    }

    private void validarPerfilActivo(Boolean activo, String tipoPerfil, Long perfilId) {
        if (!Boolean.TRUE.equals(activo)) {
            throw new AsociacionAcademicaException(
                    "El " + tipoPerfil + " con id " + perfilId + " está inactivo."
            );
        }
    }
}
