package com.umg.sgau.nota.serviceimpl;

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

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotaServiceImpl implements NotaService {

    private final NotaRepository notaRepository;
    private final InscripcionRepository inscripcionRepository;   
    
    public NotaServiceImpl(NotaRepository notaRepository, InscripcionRepository inscripcionRepository) {
        this.notaRepository = notaRepository;
        this.inscripcionRepository = inscripcionRepository;
    } 
    
    // Registrar calificaciones
    @Override
    public NotaResponseDTO registrarNota(NotaRequestDTO nuevaNota) {
        boolean yaExiste = notaRepository.findAll().stream()
                .filter(nota -> Boolean.TRUE.equals(nota.getActivo()))
                .anyMatch(nota ->
                        nota.getInscripcion() != null
                        && nuevaNota.getInscripcionId() != null &&
                        nota.getInscripcion().getId().equals(nuevaNota.getInscripcionId())
                );

        if (yaExiste) {
            throw new IllegalArgumentException("El estudiante ya tiene una calificación registrada para este curso.");
        }
        
        InscripcionEntity inscripcion = obtenerInscripcionActiva(
        		nuevaNota.getInscripcionId()
        );
        
        NotaEntity nota = NotaMapper.aEntidad(
        		nuevaNota,
        		inscripcion
        );
        
        NotaEntity notaGuardada = notaRepository.save(nota);

        return NotaMapper.aResponseDTO(notaGuardada);
    }


    // Actualizar calificaciones
    @Override
    public NotaResponseDTO actualizarNota(Long id, NotaRequestDTO notaActualizada) {
        NotaEntity notaExistente = notaRepository.findById(id)
                .filter(nota -> Boolean.TRUE.equals(nota.getActivo()))
                .orElseThrow(() -> new NotaNoEncontradaException(id));

        notaExistente.setCicloAcademico(notaActualizada.getCicloAcademico());
        notaExistente.setZona(notaActualizada.getZona());
        notaExistente.setExamenFinal(notaActualizada.getExamenFinal());
        notaExistente.setEstado(notaActualizada.getEstado());

        if (notaActualizada.getFechaRegistro() != null) {
            notaExistente.setFechaRegistro(notaActualizada.getFechaRegistro());
        }

        NotaEntity guardada = notaRepository.save(notaExistente);
        return NotaMapper.aResponseDTO(guardada);
    }

    @Override
    public void eliminar(Long id) {
        NotaEntity nota = notaRepository.findById(id)
                .orElseThrow(() -> new NotaNoEncontradaException(id));

        nota.setActivo(false);
        notaRepository.save(nota);
    }


    // Consultar notas por diferentes criterios
    @Override
    public List<NotaResponseDTO> obtenerTodas() {
    	return notaRepository.findAll()
                .stream()
                .filter(
                    nota ->
                        Boolean.TRUE.equals(nota.getActivo())
                )
                .map(NotaMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotaResponseDTO obtenerPorId(Long id) {
        NotaEntity nota = notaRepository.findById(id)
                .filter(n -> Boolean.TRUE.equals(n.getActivo()))
                .orElseThrow(() -> new NotaNoEncontradaException(id));

        return NotaMapper.aResponseDTO(nota);
    }

    @Override
    public List<NotaResponseDTO> obtenerPorEstudiante(Long estudianteId) {
        return notaRepository.findAll().stream()
                .filter(nota -> Boolean.TRUE.equals(nota.getActivo()))
                .filter(nota -> nota.getInscripcion() != null
                        && nota.getInscripcion().getEstudiante() != null
                        && nota.getInscripcion().getEstudiante().getId().equals(estudianteId))
                .map(NotaMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotaResponseDTO> obtenerPorCurso(Long cursoId) {
        return notaRepository.findAll().stream()
                .filter(nota -> Boolean.TRUE.equals(nota.getActivo()))
                .filter(nota -> nota.getInscripcion() != null
                        && nota.getInscripcion().getCurso() != null
                        && nota.getInscripcion().getCurso().getId().equals(cursoId))
                .map(NotaMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotaResponseDTO> obtenerPorInscripcion(Long inscripcionId) {
        return notaRepository.findAll().stream()
                .filter(nota -> Boolean.TRUE.equals(nota.getActivo()))
                .filter(nota -> nota.getInscripcion() != null
                        && nota.getInscripcion().getId().equals(inscripcionId))
                .map(NotaMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotaResponseDTO obtenerPorEstudianteYCurso(Long estudianteId, Long cursoId) {
        // Combinación de filtros y Excepción personalizada
        return notaRepository.findAll().stream()
                .filter(nota -> Boolean.TRUE.equals(nota.getActivo()))
                .filter(nota -> nota.getInscripcion() != null
                        && nota.getInscripcion().getEstudiante() != null
                        && nota.getInscripcion().getEstudiante().getId().equals(estudianteId)
                        && nota.getInscripcion().getCurso() != null
                        && nota.getInscripcion().getCurso().getId().equals(cursoId))
                .findFirst()
                .map(NotaMapper::aResponseDTO)
                .orElseThrow(() -> new NotaNoEncontradaException(
                    "No se encontró calificación para el estudiante ID: " + estudianteId + " en el curso ID: " + cursoId
                ));
    }
    
    private InscripcionEntity obtenerInscripcionActiva(Long inscripcionId) {

        return inscripcionRepository
                .findByIdAndActivoTrue(inscripcionId)
                .orElseThrow(
                    () -> new InscripcionNoEncontradaException(inscripcionId)
                );
    }

    // Calcular automáticamente el promedio general
    @Override
    public Double calcularPromedioEstudiante(Long estudianteId) {
         return notaRepository.findAll().stream()
                .filter(nota -> Boolean.TRUE.equals(nota.getActivo()))
                .filter(nota -> nota.getInscripcion() != null
                        && nota.getInscripcion().getEstudiante() != null
                        && nota.getInscripcion().getEstudiante().getId().equals(estudianteId)
                        && nota.getNotaFinal() != null)
                .mapToDouble(nota -> nota.getNotaFinal().doubleValue())
                .average()
                .orElse(0.0);
    }
}
