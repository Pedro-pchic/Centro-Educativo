package com.umg.sgau.curso.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.carrera.exception.CarreraNoEncontradaException;
import com.umg.sgau.carrera.repository.CarreraRepository;
import com.umg.sgau.curso.dto.CursoRequestDTO;
import com.umg.sgau.curso.dto.CursoResponseDTO;
import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.curso.exception.CursoDuplicadoException;
import com.umg.sgau.curso.exception.CursoNoEncontradoException;
import com.umg.sgau.curso.exception.DocenteInactivoParaCursoException;
import com.umg.sgau.curso.mapper.CursoMapper;
import com.umg.sgau.curso.repository.CursoRepository;
import com.umg.sgau.curso.service.CursoService;
import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.exception.DocenteNoEncontradoException;
import com.umg.sgau.docente.repository.DocenteRepository;

@Service
@Transactional
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;
    private final DocenteRepository docenteRepository;

    public CursoServiceImpl(
            CursoRepository cursoRepository,
            CarreraRepository carreraRepository,
            DocenteRepository docenteRepository) {

        this.cursoRepository = cursoRepository;
        this.carreraRepository = carreraRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    public CursoResponseDTO crear(CursoRequestDTO request) {

        String codigo = normalizarCodigo(request.getCodigo());

        if (cursoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new CursoDuplicadoException(codigo);
        }

        CarreraEntity carrera = obtenerCarreraActiva(
                request.getCarreraId()
        );

        DocenteEntity docente = obtenerDocenteActivo(
                request.getDocenteId()
        );

        CursoEntity curso = CursoMapper.aEntidad(
                request,
                carrera,
                docente
        );

        CursoEntity cursoGuardado =
                cursoRepository.save(curso);

        return CursoMapper.aResponseDTO(cursoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerPorId(Long id) {

        return cursoRepository.findByIdAndActivoTrue(id)
                .map(CursoMapper::aResponseDTO)
                .orElseThrow(
                    () -> new CursoNoEncontradoException(id)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerTodos() {

        return cursoRepository.findAll()
                .stream()
                .filter(
                    curso ->
                        Boolean.TRUE.equals(curso.getActivo())
                )
                .map(CursoMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerInactivos() {

        return cursoRepository.findAllByActivoFalse()
                .stream()
                .filter(
                    curso ->
                        Boolean.FALSE.equals(curso.getActivo())
                )
                .map(CursoMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerPorCarrera(
            Long carreraId) {

        obtenerCarreraActiva(carreraId);

        return cursoRepository
                .findAllByCarreraIdAndActivoTrue(carreraId)
                .stream()
                .filter(
                    curso ->
                        Boolean.TRUE.equals(curso.getActivo())
                )
                .map(CursoMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CursoResponseDTO actualizar(
            Long id,
            CursoRequestDTO request) {

        CursoEntity curso = cursoRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(
                    () -> new CursoNoEncontradoException(id)
                );

        String codigo = normalizarCodigo(request.getCodigo());

        cursoRepository.findByCodigoIgnoreCase(codigo)
                .filter(
                    otroCurso ->
                        !otroCurso.getId().equals(id)
                )
                .ifPresent(otroCurso -> {
                    throw new CursoDuplicadoException(codigo);
                });

        CarreraEntity carrera = obtenerCarreraActiva(
                request.getCarreraId()
        );

        DocenteEntity docente = obtenerDocenteActivo(
                request.getDocenteId()
        );

        CursoMapper.actualizarEntidad(
                curso,
                request,
                carrera,
                docente
        );

        CursoEntity cursoActualizado =
                cursoRepository.save(curso);

        return CursoMapper.aResponseDTO(cursoActualizado);
    }

    @Override
    public void eliminar(Long id) {

        CursoEntity curso = cursoRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(
                    () -> new CursoNoEncontradoException(id)
                );

        curso.setActivo(false);

        cursoRepository.save(curso);
    }

    @Override
    public CursoResponseDTO restaurar(Long id) {

        CursoEntity curso = cursoRepository.findById(id)
                .orElseThrow(
                    () -> new CursoNoEncontradoException(
                        "No se encontró un curso con el ID: " + id
                    )
                );

        if (Boolean.TRUE.equals(curso.getActivo())) {
            throw new CursoNoEncontradoException(
                "El curso con ID " + id
                + " ya se encuentra activo"
            );
        }

        CarreraEntity carrera = obtenerCarreraActiva(
                curso.getCarrera().getId()
        );

        DocenteEntity docente = obtenerDocenteActivo(
                curso.getDocente().getId()
        );

        curso.setCarrera(carrera);
        curso.setDocente(docente);
        curso.setActivo(true);

        return CursoMapper.aResponseDTO(
                cursoRepository.save(curso)
        );
    }

    private CarreraEntity obtenerCarreraActiva(
            Long carreraId) {

        return carreraRepository
                .findByIdAndActivoTrue(carreraId)
                .orElseThrow(
                    () -> new CarreraNoEncontradaException(
                            carreraId
                    )
                );
    }

    private DocenteEntity obtenerDocenteActivo(
            Long docenteId) {

        DocenteEntity docente = docenteRepository
                .findById(docenteId)
                .orElseThrow(
                    () -> new DocenteNoEncontradoException(
                            docenteId
                    )
                );

        if (!Boolean.TRUE.equals(docente.getActivo())) {
            throw new DocenteInactivoParaCursoException(
                    docenteId
            );
        }

        return docente;
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase();
    }
}
