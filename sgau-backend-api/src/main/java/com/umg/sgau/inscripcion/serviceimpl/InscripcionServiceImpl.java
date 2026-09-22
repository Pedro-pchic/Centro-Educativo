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
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.service.IdentidadAcademicaService;

import org.springframework.beans.factory.annotation.Autowired;

@Service
@Transactional
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionMapper inscripcionMapper;
    private final IdentidadAcademicaService identidadAcademicaService;

    @Autowired
    public InscripcionServiceImpl(
            InscripcionRepository inscripcionRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository,
            InscripcionMapper inscripcionMapper,
            IdentidadAcademicaService identidadAcademicaService) {
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.inscripcionMapper = inscripcionMapper;
        this.identidadAcademicaService = identidadAcademicaService;
    }

    public InscripcionServiceImpl(
            InscripcionRepository inscripcionRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository,
            InscripcionMapper inscripcionMapper) {
        this(inscripcionRepository, estudianteRepository, cursoRepository,
                inscripcionMapper, null);
    }

    @Override
    public InscripcionResponseDTO crear(InscripcionRequestDTO request) {
        requerirAdmin();
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
                .map(inscripcion -> {
                    validarAccesoInscripcion(inscripcion);
                    return inscripcionMapper.aResponseDTO(inscripcion);
                })
                .orElseThrow(() -> new InscripcionNoEncontradaException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerTodas() {
        requerirAdmin();
        return inscripcionRepository.findAllByActivoTrue()
                .stream()
                .filter(inscripcion -> Boolean.TRUE.equals(inscripcion.getActivo()))
                .map(inscripcionMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InscripcionResponseDTO> obtenerTodasPaginadas(int pagina, int tamanio) {
        requerirAdmin();
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
        validarAccesoEstudiante(estudianteId);
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
        CursoEntity curso = obtenerCursoActivo(cursoId);
        validarAccesoCurso(curso);
        return inscripcionRepository.findByCurso_IdAndActivoTrue(cursoId)
                .stream()
                .filter(inscripcion -> Boolean.TRUE.equals(inscripcion.getActivo()))
                .map(inscripcionMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InscripcionResponseDTO actualizar(Long id, InscripcionRequestDTO request) {
        requerirAdmin();
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
        requerirAdmin();
        InscripcionEntity inscripcion = inscripcionRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new InscripcionNoEncontradaException(id));
        inscripcion.setActivo(false);
        inscripcion.setFechaActualizacion(LocalDateTime.now());
        inscripcionRepository.save(inscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerMisInscripciones() {
        if (identidadAcademicaService == null) {
            throw new AsociacionAcademicaException(
                    "No se configuró el resolver de identidad académica.");
        }

        Long estudianteId = identidadAcademicaService.obtenerEstudianteAutenticado().getId();
        return inscripcionRepository.findByEstudiante_IdAndActivoTrue(estudianteId)
                .stream()
                .map(inscripcionMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    private void requerirAdmin() {
        if (identidadAcademicaService != null
                && !identidadAcademicaService.esAdmin()) {
            throw new AsociacionAcademicaException(
                    "Solo ADMIN puede administrar inscripciones.");
        }
    }

    private void validarAccesoEstudiante(Long estudianteId) {
        if (identidadAcademicaService == null) {
            return;
        }

        UsuarioEntity usuario = identidadAcademicaService.obtenerUsuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (usuario.getRol() == RolUsuario.ESTUDIANTE
                && identidadAcademicaService.obtenerEstudianteAutenticado()
                        .getId().equals(estudianteId)) {
            return;
        }
        throw new AsociacionAcademicaException(
                "No tiene permisos para consultar inscripciones de este estudiante.");
    }

    private void validarAccesoCurso(CursoEntity curso) {
        if (identidadAcademicaService == null
                || identidadAcademicaService.esAdmin()) {
            return;
        }

        Long docenteId = curso.getDocente() == null
                ? null
                : curso.getDocente().getId();
        if (docenteId != null
                && identidadAcademicaService.obtenerDocenteAutenticado()
                        .getId().equals(docenteId)) {
            return;
        }
        throw new AsociacionAcademicaException(
                "No tiene permisos para consultar estudiantes de este curso.");
    }

    private void validarAccesoInscripcion(InscripcionEntity inscripcion) {
        if (identidadAcademicaService == null) {
            return;
        }

        UsuarioEntity usuario = identidadAcademicaService.obtenerUsuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (usuario.getRol() == RolUsuario.ESTUDIANTE
                && inscripcion.getEstudiante() != null
                && identidadAcademicaService.obtenerEstudianteAutenticado()
                        .getId().equals(inscripcion.getEstudiante().getId())) {
            return;
        }
        if (usuario.getRol() == RolUsuario.DOCENTE
                && inscripcion.getCurso() != null
                && inscripcion.getCurso().getDocente() != null
                && identidadAcademicaService.obtenerDocenteAutenticado()
                        .getId().equals(inscripcion.getCurso().getDocente().getId())) {
            return;
        }
        throw new AsociacionAcademicaException(
                "No tiene permisos para consultar esta inscripción.");
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
