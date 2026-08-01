package com.umg.sgau.carrera.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umg.sgau.carrera.dto.CarreraRequestDTO;
import com.umg.sgau.carrera.dto.CarreraResponseDTO;
import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.carrera.exception.CarreraDuplicadaException;
import com.umg.sgau.carrera.exception.CarreraNoEncontradaException;
import com.umg.sgau.carrera.mapper.CarreraMapper;
import com.umg.sgau.carrera.repository.CarreraRepository;
import com.umg.sgau.carrera.service.CarreraService;

@Service
@Transactional
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraServiceImpl(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    public CarreraResponseDTO crear(CarreraRequestDTO request) {
        String codigo = normalizarCodigo(request.getCodigo());
        if (carreraRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new CarreraDuplicadaException(codigo);
        }

        CarreraEntity carrera = CarreraMapper.aEntidad(request);

        return CarreraMapper.aResponseDTO(carreraRepository.save(carrera));
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraResponseDTO obtenerPorId(Long id) {
        return carreraRepository.findByIdAndActivoTrue(id)
                .map(CarreraMapper::aResponseDTO)
                .orElseThrow(() -> new CarreraNoEncontradaException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> obtenerTodos() {
        return carreraRepository.findAll()
                .stream()
                .filter(carrera -> Boolean.TRUE.equals(carrera.getActivo()))
                .map(CarreraMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> obtenerInactivos() {
        return carreraRepository.findAllByActivoFalse()
                .stream()
                .filter(carrera -> Boolean.FALSE.equals(carrera.getActivo()))
                .map(CarreraMapper::aResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CarreraResponseDTO actualizar(Long id, CarreraRequestDTO request) {
        CarreraEntity carrera = carreraRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new CarreraNoEncontradaException(id));
        String codigo = normalizarCodigo(request.getCodigo());

        carreraRepository.findByCodigoIgnoreCase(codigo)
                .filter(otraCarrera -> !otraCarrera.getId().equals(id))
                .ifPresent(otraCarrera -> {
                    throw new CarreraDuplicadaException(codigo);
                });

        CarreraMapper.actualizarEntidad(carrera, request);
        return CarreraMapper.aResponseDTO(carreraRepository.save(carrera));
    }

    @Override
    public void eliminar(Long id) {
        CarreraEntity carrera = carreraRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new CarreraNoEncontradaException(id));
        carrera.setActivo(false);
        carreraRepository.save(carrera);
    }

    @Override
    public CarreraResponseDTO restaurar(Long id) {
        CarreraEntity carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new CarreraNoEncontradaException(
                        "No se encontró una carrera con el ID: " + id));
        carrera.setActivo(true);
        return CarreraMapper.aResponseDTO(carreraRepository.save(carrera));
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase();
    }

}
