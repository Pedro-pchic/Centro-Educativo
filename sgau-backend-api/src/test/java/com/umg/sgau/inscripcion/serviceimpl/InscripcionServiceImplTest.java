package com.umg.sgau.inscripcion.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.curso.exception.CursoNoEncontradoException;
import com.umg.sgau.curso.repository.CursoRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.exception.EstudianteNoEncontradoException;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.inscripcion.dto.InscripcionRequestDTO;
import com.umg.sgau.inscripcion.entity.InscripcionEntity;
import com.umg.sgau.inscripcion.exception.InscripcionDuplicadaException;
import com.umg.sgau.inscripcion.exception.InscripcionNoEncontradaException;
import com.umg.sgau.inscripcion.exception.InscripcionRelacionInactivaException;
import com.umg.sgau.inscripcion.mapper.InscripcionMapper;
import com.umg.sgau.inscripcion.repository.InscripcionRepository;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceImplTest {

    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private CursoRepository cursoRepository;

    private InscripcionServiceImpl service;
    private EstudianteEntity estudiante;
    private CursoEntity curso;
    private InscripcionEntity inscripcion;
    private InscripcionRequestDTO request;

    @BeforeEach
    void preparar() {
        service = new InscripcionServiceImpl(
                inscripcionRepository, estudianteRepository, cursoRepository,
                new InscripcionMapper());

        estudiante = new EstudianteEntity();
        estudiante.setId(1L);
        estudiante.setCodigoEstudiante("EST-1");
        estudiante.setNombre("Ana");
        estudiante.setApellido("López");
        estudiante.setActivo(true);

        curso = CursoEntity.builder().id(2L).nombre("Programación").activo(true).build();

        inscripcion = new InscripcionEntity();
        inscripcion.setId(3L);
        inscripcion.setEstudiante(estudiante);
        inscripcion.setCurso(curso);
        inscripcion.setFechaInscripcion(LocalDate.of(2026, 8, 1));
        inscripcion.setActivo(true);

        request = new InscripcionRequestDTO();
        request.setEstudianteId(1L);
        request.setCursoId(2L);
        request.setFechaInscripcion(LocalDate.of(2026, 8, 1));
    }

    @Test
    void creaInscripcionConRelacionesActivas() {
        relacionesExistentes();
        when(inscripcionRepository.save(any())).thenAnswer(invocation -> {
            InscripcionEntity entity = invocation.getArgument(0);
            entity.setId(3L);
            return entity;
        });

        var response = service.crear(request);

        assertEquals(3L, response.getId());
        assertEquals("Ana López", response.getNombreEstudiante());
        assertTrue(response.getActivo());
    }

    @Test
    void rechazaEstudianteInexistente() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EstudianteNoEncontradoException.class, () -> service.crear(request));
    }

    @Test
    void rechazaCursoInexistente() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(cursoRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(CursoNoEncontradoException.class, () -> service.crear(request));
    }

    @Test
    void rechazaEstudianteInactivo() {
        estudiante.setActivo(false);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        assertThrows(InscripcionRelacionInactivaException.class, () -> service.crear(request));
    }

    @Test
    void rechazaCursoInactivo() {
        curso.setActivo(false);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
        assertThrows(InscripcionRelacionInactivaException.class, () -> service.crear(request));
    }

    @Test
    void rechazaInscripcionDuplicada() {
        relacionesExistentes();
        when(inscripcionRepository.existsByEstudiante_IdAndCurso_IdAndActivoTrue(1L, 2L))
                .thenReturn(true);
        assertThrows(InscripcionDuplicadaException.class, () -> service.crear(request));
    }

    @Test
    void obtieneActivaPorIdYRechazaInexistente() {
        when(inscripcionRepository.findByIdAndActivoTrue(3L))
                .thenReturn(Optional.of(inscripcion), Optional.empty());
        assertEquals(3L, service.obtenerPorId(3L).getId());
        assertThrows(InscripcionNoEncontradaException.class, () -> service.obtenerPorId(3L));
    }

    @Test
    void listaSoloActivas() {
        InscripcionEntity inactiva = nuevaInscripcion(4L, false);
        when(inscripcionRepository.findAllByActivoTrue())
                .thenReturn(List.of(inscripcion, inactiva));
        assertEquals(1, service.obtenerTodas().size());
    }

    @Test
    void listaPorEstudianteYPorCurso() {
        relacionesExistentes();
        when(inscripcionRepository.findByEstudiante_IdAndActivoTrue(1L))
                .thenReturn(List.of(inscripcion));
        when(inscripcionRepository.findByCurso_IdAndActivoTrue(2L))
                .thenReturn(List.of(inscripcion));
        assertEquals(1, service.obtenerPorEstudiante(1L).size());
        assertEquals(1, service.obtenerPorCurso(2L).size());
    }

    @Test
    void paginaEnRepositorioYValidaParametros() {
        when(inscripcionRepository.findAllByActivoTrue(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(inscripcion)));
        assertEquals(1, service.obtenerTodasPaginadas(0, 10).getTotalElements());
        assertThrows(RuntimeException.class, () -> service.obtenerTodasPaginadas(-1, 10));
        assertThrows(RuntimeException.class, () -> service.obtenerTodasPaginadas(0, 0));
    }

    @Test
    void actualizaConservandoIdYEstado() {
        relacionesExistentes();
        when(inscripcionRepository.findByIdAndActivoTrue(3L)).thenReturn(Optional.of(inscripcion));
        when(inscripcionRepository.save(inscripcion)).thenReturn(inscripcion);
        request.setFechaInscripcion(LocalDate.of(2026, 9, 1));

        var response = service.actualizar(3L, request);

        assertEquals(3L, response.getId());
        assertTrue(response.getActivo());
        assertEquals(LocalDate.of(2026, 9, 1), response.getFechaInscripcion());
    }

    @Test
    void impideDuplicadoDuranteActualizacion() {
        relacionesExistentes();
        when(inscripcionRepository.findByIdAndActivoTrue(3L)).thenReturn(Optional.of(inscripcion));
        when(inscripcionRepository.existsByEstudiante_IdAndCurso_IdAndActivoTrueAndIdNot(1L, 2L, 3L))
                .thenReturn(true);
        assertThrows(InscripcionDuplicadaException.class,
                () -> service.actualizar(3L, request));
    }

    @Test
    void softDeleteGuardaSinEliminarFisicamenteYSegundaEliminacionFalla() {
        when(inscripcionRepository.findByIdAndActivoTrue(3L))
                .thenReturn(Optional.of(inscripcion), Optional.empty());
        when(inscripcionRepository.save(inscripcion)).thenReturn(inscripcion);

        service.eliminar(3L);

        assertFalse(inscripcion.getActivo());
        verify(inscripcionRepository).save(inscripcion);
        verify(inscripcionRepository, never()).delete(any());
        verify(inscripcionRepository, never()).deleteById(anyLong());
        assertThrows(InscripcionNoEncontradaException.class, () -> service.eliminar(3L));
    }

    private void relacionesExistentes() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
    }

    private InscripcionEntity nuevaInscripcion(Long id, boolean activo) {
        InscripcionEntity entity = new InscripcionEntity();
        entity.setId(id);
        entity.setEstudiante(estudiante);
        entity.setCurso(curso);
        entity.setActivo(activo);
        return entity;
    }
}
