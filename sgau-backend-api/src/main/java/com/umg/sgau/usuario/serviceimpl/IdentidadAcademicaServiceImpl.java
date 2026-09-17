package com.umg.sgau.usuario.serviceimpl;

import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import com.umg.sgau.usuario.service.IdentidadAcademicaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IdentidadAcademicaServiceImpl implements IdentidadAcademicaService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;

    public IdentidadAcademicaServiceImpl(
            UsuarioRepository usuarioRepository,
            EstudianteRepository estudianteRepository,
            DocenteRepository docenteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    public UsuarioEntity obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new AsociacionAcademicaException(
                    "No se pudo resolver el usuario autenticado.");
        }

        UsuarioEntity usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AsociacionAcademicaException(
                        "El usuario autenticado no existe."));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new AsociacionAcademicaException(
                    "El usuario autenticado está inactivo.");
        }

        return usuario;
    }

    @Override
    public EstudianteEntity obtenerEstudianteAutenticado() {
        UsuarioEntity usuario = obtenerUsuarioAutenticado();
        validarRol(usuario, RolUsuario.ESTUDIANTE);

        return estudianteRepository.findByUsuarioIdAndActivoTrue(usuario.getId())
                .orElseThrow(() -> new AsociacionAcademicaException(
                        "El usuario ESTUDIANTE no tiene un perfil académico asociado."));
    }

    @Override
    public DocenteEntity obtenerDocenteAutenticado() {
        UsuarioEntity usuario = obtenerUsuarioAutenticado();
        validarRol(usuario, RolUsuario.DOCENTE);

        return docenteRepository.findByUsuarioIdAndActivoTrue(usuario.getId())
                .orElseThrow(() -> new AsociacionAcademicaException(
                        "El usuario DOCENTE no tiene un perfil académico asociado."));
    }

    @Override
    public boolean esAdmin() {
        return obtenerUsuarioAutenticado().getRol() == RolUsuario.ADMIN;
    }

    private void validarRol(UsuarioEntity usuario, RolUsuario rolRequerido) {
        if (usuario.getRol() != rolRequerido) {
            throw new AsociacionAcademicaException(
                    "El usuario autenticado no tiene el rol " + rolRequerido.name() + ".");
        }
    }
}
