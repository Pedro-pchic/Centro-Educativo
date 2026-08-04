package com.umg.sgau.carrera.service;

import java.util.List;

import com.umg.sgau.carrera.dto.CarreraRequestDTO;
import com.umg.sgau.carrera.dto.CarreraResponseDTO;

public interface CarreraService {

    CarreraResponseDTO crear(CarreraRequestDTO request);

    CarreraResponseDTO obtenerPorId(Long id);

    List<CarreraResponseDTO> obtenerTodos();

    List<CarreraResponseDTO> obtenerInactivos();

    CarreraResponseDTO actualizar(Long id, CarreraRequestDTO request);

    void eliminar(Long id);

    CarreraResponseDTO restaurar(Long id);
}
