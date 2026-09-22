package com.umg.sgau.nota.serviceimpl;

import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.curso.exception.CursoNoEncontradoException;
import com.umg.sgau.curso.repository.CursoRepository;
import com.umg.sgau.inscripcion.entity.InscripcionEntity;
import com.umg.sgau.inscripcion.exception.InscripcionNoEncontradaException;
import com.umg.sgau.inscripcion.repository.InscripcionRepository;
import com.umg.sgau.nota.dto.NotaRequestDTO;
import com.umg.sgau.nota.dto.NotaResponseDTO;
import com.umg.sgau.nota.entity.NotaEntity;
import com.umg.sgau.nota.exception.NotaNoEncontradaException;
import com.umg.sgau.nota.mapper.NotaMapper;
import com.umg.sgau.nota.repository.NotaRepository;
import com.umg.sgau.nota.service.NotaService;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.service.IdentidadAcademicaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotaServiceImpl implements NotaService {

    private final NotaRepository notaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final IdentidadAcademicaService identidadAcademicaService;

    @Autowired
    public NotaServiceImpl(
            NotaRepository notaRepository,
            InscripcionRepository inscripcionRepository,
            CursoRepository cursoRepository,
            IdentidadAcademicaService identidadAcademicaService) {
        this.notaRepository = notaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.cursoRepository = cursoRepository;
        this.identidadAcademicaService = identidadAcademicaService;
    }

    public NotaServiceImpl(
            NotaRepository notaRepository,
            InscripcionRepository inscripcionRepository) {
        this(notaRepository, inscripcionRepository, null, null);
    }

    @Override
    public NotaResponseDTO registrarNota(NotaRequestDTO nuevaNota) {
        validarValoresNota(nuevaNota);
        InscripcionEntity inscripcion = obtenerInscripcionActiva(
                nuevaNota.getInscripcionId());
        validarAccesoInscripcion(inscripcion);

        if (notaRepository.existsByInscripcionIdAndActivoTrue(
                nuevaNota.getInscripcionId())) {
            throw new IllegalArgumentException(
                    "El estudiante ya tiene una calificación registrada para este curso.");
        }

        NotaEntity nota = NotaMapper.aEntidad(nuevaNota, inscripcion);
        return NotaMapper.aResponseDTO(notaRepository.save(nota));
    }

    @Override
    public NotaResponseDTO actualizarNota(Long id, NotaRequestDTO notaActualizada) {
        validarValoresNota(notaActualizada);
        NotaEntity notaExistente = notaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new NotaNoEncontradaException(id));
        validarAccesoNota(notaExistente);

        notaExistente.setCicloAcademico(notaActualizada.getCicloAcademico());
        notaExistente.setZona(notaActualizada.getZona());
        notaExistente.setExamenFinal(notaActualizada.getExamenFinal());
        notaExistente.setEstado(notaActualizada.getEstado());

        if (notaActualizada.getFechaRegistro() != null) {
            notaExistente.setFechaRegistro(notaActualizada.getFechaRegistro());
        }

        return NotaMapper.aResponseDTO(notaRepository.saveAndFlush(notaExistente));
    }

    @Override
    public void eliminar(Long id) {
        requerirAdmin();
        NotaEntity nota = notaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new NotaNoEncontradaException(id));
        nota.setActivo(false);
        notaRepository.save(nota);
    }

    @Override
    public List<NotaResponseDTO> obtenerTodas() {
        requerirAdmin();
        return mapear(notaRepository.findByActivoTrue());
    }

    @Override
    public NotaResponseDTO obtenerPorId(Long id) {
        NotaEntity nota = notaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new NotaNoEncontradaException(id));
        validarAccesoNota(nota);
        return NotaMapper.aResponseDTO(nota);
    }

    @Override
    public List<NotaResponseDTO> obtenerPorEstudiante(Long estudianteId) {
        UsuarioEntity usuario = usuarioAutenticado();
        List<NotaEntity> notas;

        if (usuario.getRol() == RolUsuario.ADMIN) {
            notas = notaRepository.findByActivoTrueAndInscripcion_Estudiante_Id(estudianteId);
        } else if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
            validarEstudiantePropietario(estudianteId);
            notas = notaRepository.findByActivoTrueAndInscripcion_Estudiante_Id(estudianteId);
        } else if (usuario.getRol() == RolUsuario.DOCENTE) {
            Long docenteId = identidadAcademicaService.obtenerDocenteAutenticado().getId();
            notas = notaRepository
                    .findByActivoTrueAndInscripcion_Estudiante_IdAndInscripcion_Curso_Docente_Id(
                            estudianteId, docenteId);
        } else {
            throw accesoDenegado("No tiene permisos para consultar estas notas.");
        }

        return mapear(notas);
    }

    @Override
    public List<NotaResponseDTO> obtenerMisNotas() {
        Long estudianteId = identidadAcademicaService.obtenerEstudianteAutenticado().getId();
        return mapear(notaRepository.findByActivoTrueAndInscripcion_Estudiante_Id(estudianteId));
    }

    @Override
    public List<NotaResponseDTO> obtenerPorCurso(Long cursoId) {
        CursoEntity curso = cursoRepository == null
                ? null
                : cursoRepository.findByIdAndActivoTrue(cursoId)
                        .orElseThrow(() -> new CursoNoEncontradoException(cursoId));
        validarAccesoCurso(curso);
        return mapear(notaRepository.findByActivoTrueAndInscripcion_Curso_Id(cursoId));
    }

    @Override
    public List<NotaResponseDTO> obtenerPorInscripcion(Long inscripcionId) {
        InscripcionEntity inscripcion = obtenerInscripcionActiva(inscripcionId);
        validarAccesoInscripcion(inscripcion);
        return mapear(notaRepository.findByActivoTrueAndInscripcion_Id(inscripcionId));
    }

    @Override
    public NotaResponseDTO obtenerPorEstudianteYCurso(Long estudianteId, Long cursoId) {
        validarEstudianteCursoPropietario(estudianteId, cursoId);
        return notaRepository
                .findFirstByActivoTrueAndInscripcion_Estudiante_IdAndInscripcion_Curso_Id(
                        estudianteId, cursoId)
                .map(nota -> {
                    validarAccesoNota(nota);
                    return NotaMapper.aResponseDTO(nota);
                })
                .orElseThrow(() -> new NotaNoEncontradaException(
                        "No se encontró calificación para el estudiante ID: "
                                + estudianteId + " en el curso ID: " + cursoId));
    }

    @Override
    public Double calcularPromedioEstudiante(Long estudianteId) {
        UsuarioEntity usuario = usuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
            validarEstudiantePropietario(estudianteId);
        } else if (usuario.getRol() != RolUsuario.ADMIN) {
            throw accesoDenegado("No tiene permisos para consultar este promedio.");
        }

        return promedio(notaRepository.findByActivoTrueAndInscripcion_Estudiante_Id(estudianteId));
    }

    @Override
    public Double calcularMiPromedio() {
        Long estudianteId = identidadAcademicaService.obtenerEstudianteAutenticado().getId();
        return promedio(notaRepository.findByActivoTrueAndInscripcion_Estudiante_Id(estudianteId));
    }

    private InscripcionEntity obtenerInscripcionActiva(Long inscripcionId) {
        return inscripcionRepository.findByIdAndActivoTrue(inscripcionId)
                .orElseThrow(() -> new InscripcionNoEncontradaException(inscripcionId));
    }

    private void validarAccesoNota(NotaEntity nota) {
        validarAccesoInscripcion(nota.getInscripcion());
    }

    private void validarAccesoInscripcion(InscripcionEntity inscripcion) {
        if (identidadAcademicaService == null) {
            return;
        }

        UsuarioEntity usuario = usuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }

        if (usuario.getRol() == RolUsuario.ESTUDIANTE
                && inscripcion != null
                && inscripcion.getEstudiante() != null
                && identidadAcademicaService.obtenerEstudianteAutenticado()
                        .getId().equals(inscripcion.getEstudiante().getId())) {
            return;
        }

        if (usuario.getRol() == RolUsuario.DOCENTE
                && inscripcion != null
                && inscripcion.getCurso() != null
                && inscripcion.getCurso().getDocente() != null
                && identidadAcademicaService.obtenerDocenteAutenticado()
                        .getId().equals(inscripcion.getCurso().getDocente().getId())) {
            return;
        }

        throw accesoDenegado("No tiene permisos para consultar o modificar esta nota.");
    }

    private void validarAccesoCurso(CursoEntity curso) {
        if (identidadAcademicaService == null) {
            return;
        }
        UsuarioEntity usuario = usuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (usuario.getRol() == RolUsuario.DOCENTE
                && curso != null
                && curso.getDocente() != null
                && identidadAcademicaService.obtenerDocenteAutenticado()
                        .getId().equals(curso.getDocente().getId())) {
            return;
        }
        throw accesoDenegado("No tiene permisos para consultar notas de este curso.");
    }

    private void validarEstudianteCursoPropietario(Long estudianteId, Long cursoId) {
        if (identidadAcademicaService == null) {
            return;
        }
        UsuarioEntity usuario = usuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
            validarEstudiantePropietario(estudianteId);
            return;
        }
        if (usuario.getRol() == RolUsuario.DOCENTE) {
            CursoEntity curso = cursoRepository.findByIdAndActivoTrue(cursoId)
                    .orElseThrow(() -> new CursoNoEncontradoException(cursoId));
            validarAccesoCurso(curso);
            return;
        }
        throw accesoDenegado("No tiene permisos para consultar esta nota.");
    }

    private void validarEstudiantePropietario(Long estudianteId) {
        if (!identidadAcademicaService.obtenerEstudianteAutenticado()
                .getId().equals(estudianteId)) {
            throw accesoDenegado("No tiene permisos para consultar datos de este estudiante.");
        }
    }

    private UsuarioEntity usuarioAutenticado() {
        if (identidadAcademicaService == null) {
            throw new AsociacionAcademicaException(
                    "No se configuró el resolver de identidad académica.");
        }
        return identidadAcademicaService.obtenerUsuarioAutenticado();
    }

    private void requerirAdmin() {
        if (identidadAcademicaService != null
                && !identidadAcademicaService.esAdmin()) {
            throw accesoDenegado("Solo ADMIN puede administrar notas.");
        }
    }

    private void validarValoresNota(NotaRequestDTO nota) {
        if (nota.getZona() != null
                && (nota.getZona().compareTo(BigDecimal.ZERO) < 0
                        || nota.getZona().compareTo(new BigDecimal("70.00")) > 0)) {
            throw new IllegalArgumentException(
                    "La zona debe estar entre 0 y 70.");
        }
        if (nota.getExamenFinal() != null
                && (nota.getExamenFinal().compareTo(BigDecimal.ZERO) < 0
                        || nota.getExamenFinal().compareTo(new BigDecimal("30.00")) > 0)) {
            throw new IllegalArgumentException(
                    "El examen final debe estar entre 0 y 30.");
        }
    }

    private AsociacionAcademicaException accesoDenegado(String mensaje) {
        return new AsociacionAcademicaException(mensaje);
    }

    private List<NotaResponseDTO> mapear(List<NotaEntity> notas) {
        return notas.stream()
                .map(NotaMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    private Double promedio(List<NotaEntity> notas) {
        return notas.stream()
                .map(NotaEntity::getNotaFinal)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(value -> value.doubleValue())
                .average()
                .orElse(0.0);
    }
}
