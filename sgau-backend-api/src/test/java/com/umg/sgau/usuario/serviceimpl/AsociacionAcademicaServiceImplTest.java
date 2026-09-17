package com.umg.sgau.usuario.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.exception.UsuarioNoEncontradoException;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsociacionAcademicaServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private DocenteRepository docenteRepository;

    private AsociacionAcademicaServiceImpl service;
    private UsuarioEntity usuarioEstudiante;
    private UsuarioEntity usuarioDocente;
    private UsuarioEntity usuarioAdmin;
    private EstudianteEntity estudiante;
    private DocenteEntity docente;

    @BeforeEach
    void preparar() {
        service = new AsociacionAcademicaServiceImpl(
                usuarioRepository,
                estudianteRepository,
                docenteRepository
        );

        usuarioEstudiante = usuario(1L, RolUsuario.ESTUDIANTE, true);
        usuarioDocente = usuario(2L, RolUsuario.DOCENTE, true);
        usuarioAdmin = usuario(3L, RolUsuario.ADMIN, true);

        estudiante = new EstudianteEntity();
        estudiante.setId(10L);
        estudiante.setActivo(true);

        docente = new DocenteEntity();
        docente.setId(20L);
        docente.setActivo(true);
    }

    @Test
    void asociaEstudianteValido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEstudiante));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(estudianteRepository.existsByUsuarioId(1L)).thenReturn(false);
        when(docenteRepository.existsByUsuarioId(1L)).thenReturn(false);

        service.asociarEstudiante(1L, 10L);

        assertEquals(usuarioEstudiante, estudiante.getUsuario());
        verify(estudianteRepository).save(estudiante);
    }

    @Test
    void asociaDocenteValido() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioDocente));
        when(docenteRepository.findById(20L)).thenReturn(Optional.of(docente));
        when(docenteRepository.existsByUsuarioId(2L)).thenReturn(false);
        when(estudianteRepository.existsByUsuarioId(2L)).thenReturn(false);

        service.asociarDocente(2L, 20L);

        assertEquals(usuarioDocente, docente.getUsuario());
        verify(docenteRepository).save(docente);
    }

    @Test
    void rechazaAdminComoPerfilAcademico() {
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuarioAdmin));

        assertThrows(AsociacionAcademicaException.class,
                () -> service.asociarEstudiante(3L, 10L));
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void rechazaRolEstudianteParaDocente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEstudiante));

        assertThrows(AsociacionAcademicaException.class,
                () -> service.asociarDocente(1L, 20L));
        verify(docenteRepository, never()).save(any());
    }

    @Test
    void rechazaRolDocenteParaEstudiante() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioDocente));

        assertThrows(AsociacionAcademicaException.class,
                () -> service.asociarEstudiante(2L, 10L));
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void rechazaPerfilYaAsociado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEstudiante));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        estudiante.setUsuario(usuarioEstudiante);

        assertThrows(AsociacionAcademicaException.class,
                () -> service.asociarEstudiante(1L, 10L));
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void rechazaUsuarioYaAsociadoAOtroPerfilDelMismoTipo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEstudiante));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(estudianteRepository.existsByUsuarioId(1L)).thenReturn(true);

        assertThrows(AsociacionAcademicaException.class,
                () -> service.asociarEstudiante(1L, 10L));
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void rechazaUsuarioAsociadoAlOtroTipoDePerfil() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEstudiante));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(estudianteRepository.existsByUsuarioId(1L)).thenReturn(false);
        when(docenteRepository.existsByUsuarioId(1L)).thenReturn(true);

        assertThrows(AsociacionAcademicaException.class,
                () -> service.asociarEstudiante(1L, 10L));
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void rechazaUsuarioInactivo() {
        usuarioEstudiante.setActivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEstudiante));

        assertThrows(UsuarioNoEncontradoException.class,
                () -> service.asociarEstudiante(1L, 10L));
        verify(estudianteRepository, never()).findById(any());
    }

    @Test
    void rechazaPerfilInactivo() {
        estudiante.setActivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEstudiante));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));

        assertThrows(AsociacionAcademicaException.class,
                () -> service.asociarEstudiante(1L, 10L));
        verify(estudianteRepository, never()).save(any());
    }

    private UsuarioEntity usuario(Long id, RolUsuario rol, boolean activo) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuario;
    }
}
