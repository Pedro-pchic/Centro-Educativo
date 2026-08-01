package com.umg.sgau.inscripcion.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.curso.exception.CursoNoEncontradoException;
import com.umg.sgau.curso.repository.CursoRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.exception.EstudianteNoEncontradoException;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.inscripcion.dto.InscripcionRequestDTO;
import com.umg.sgau.inscripcion.dto.InscripcionResponseDTO;
import com.umg.sgau.inscripcion.entity.InscripcionEntity;
import com.umg.sgau.inscripcion.exception.InscripcionDuplicadaException;
import com.umg.sgau.inscripcion.exception.InscripcionNoEncontradaException;
import com.umg.sgau.inscripcion.exception.InscripcionRelacionInactivaException;
import com.umg.sgau.inscripcion.mapper.InscripcionMapper;
import com.umg.sgau.inscripcion.repository.InscripcionRepository;
import com.umg.sgau.inscripcion.service.InscripcionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionMapper inscripcionMapper;

    @Override
    public InscripcionResponseDTO crear(InscripcionRequestDTO request) {
        EstudianteEntity estudiante = obtenerEstudianteActivo(request.getEstudianteId());
        CursoEntity curso = obtenerCursoActivo(request.getCursoId());
        validarDuplicado(request.getEstudianteId(), request.getCursoId());

        InscripcionEntity inscripcion = inscripcionMapper.aEntidad(request, estudiante, curso);
        return inscripcionMapper.aResponseDTO(inscripcionRepository.save(inscripcion));
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponseDTO obtenerPorId(Long id) {
        return inscripcionRepository.findByIdAndActivoTrue(id)
                .map(inscripcionMapper::aResponseDTO)
                .orElseThrow(() -> new InscripcionNoEncontradaException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerTodas() {
        return inscripcionRepository.findAllByActivoTrue()
                .stream()
                .filter(inscripcion -> Boolean.TRUE.equals(inscripcion.getActivo()))
                .map(inscripcionMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InscripcionResponseDTO> obtenerTodasPaginadas(int pagina, int tamanio) {
        if (pagina < 0 || tamanio <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La página debe ser mayor o igual a cero y el tamaño mayor que cero");
        }
        return inscripcionRepository.findAllByActivoTrue(PageRequest.of(pagina, tamanio))
                .map(inscripcionMapper::aResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerPorEstudiante(Long estudianteId) {
        obtenerEstudianteActivo(estudianteId);
        return inscripcionRepository.findByEstudiante_IdAndActivoTrue(estudianteId)
                .stream()
                .filter(inscripcion -> Boolean.TRUE.equals(inscripcion.getActivo()))
                .map(inscripcionMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerPorCurso(Long cursoId) {
        obtenerCursoActivo(cursoId);
        return inscripcionRepository.findByCurso_IdAndActivoTrue(cursoId)
                .stream()
                .filter(inscripcion -> Boolean.TRUE.equals(inscripcion.getActivo()))
                .map(inscripcionMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InscripcionResponseDTO actualizar(Long id, InscripcionRequestDTO request) {
        InscripcionEntity inscripcion = inscripcionRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new InscripcionNoEncontradaException(id));
        EstudianteEntity estudiante = obtenerEstudianteActivo(request.getEstudianteId());
        CursoEntity curso = obtenerCursoActivo(request.getCursoId());

        if (inscripcionRepository.existsByEstudiante_IdAndCurso_IdAndActivoTrueAndIdNot(
                request.getEstudianteId(), request.getCursoId(), id)) {
            throw new InscripcionDuplicadaException(
                    request.getEstudianteId(), request.getCursoId());
        }

        inscripcionMapper.actualizarEntidad(inscripcion, request, estudiante, curso);
        inscripcion.setFechaActualizacion(LocalDateTime.now());
        return inscripcionMapper.aResponseDTO(inscripcionRepository.save(inscripcion));
    }

    @Override
    public void eliminar(Long id) {
        InscripcionEntity inscripcion = inscripcionRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new InscripcionNoEncontradaException(id));
        inscripcion.setActivo(false);
        inscripcion.setFechaActualizacion(LocalDateTime.now());
        inscripcionRepository.save(inscripcion);
    }

    private EstudianteEntity obtenerEstudianteActivo(Long id) {
        EstudianteEntity estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new EstudianteNoEncontradoException(id));
        if (!Boolean.TRUE.equals(estudiante.getActivo())) {
            throw new InscripcionRelacionInactivaException(
                    "No se puede crear o actualizar la inscripción porque el estudiante está inactivo");
        }
        return estudiante;
    }

    private CursoEntity obtenerCursoActivo(Long id) {
        CursoEntity curso = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNoEncontradoException(id));
        if (!Boolean.TRUE.equals(curso.getActivo())) {
            throw new InscripcionRelacionInactivaException(
                    "No se puede crear o actualizar la inscripción porque el curso está inactivo");
        }
        return curso;
    }

    private void validarDuplicado(Long estudianteId, Long cursoId) {
        if (inscripcionRepository.existsByEstudiante_IdAndCurso_IdAndActivoTrue(
                estudianteId, cursoId)) {
            throw new InscripcionDuplicadaException(estudianteId, cursoId);
        }
    }
}
