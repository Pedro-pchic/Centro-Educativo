package com.umg.sgau.colegiatura.service;

import com.umg.sgau.colegiatura.dto.ColegiaturaRequestDTO;
import com.umg.sgau.colegiatura.dto.ColegiaturaResponseDTO;

import java.util.List;

public interface ColegiaturaService {


    ColegiaturaResponseDTO crear(
            ColegiaturaRequestDTO request
    );


    ColegiaturaResponseDTO obtenerPorId(
            Long id
    );


    List<ColegiaturaResponseDTO> obtenerTodos();


    ColegiaturaResponseDTO actualizar(
            Long id,
            ColegiaturaRequestDTO request
    );


    void eliminar(
            Long id
    );

}