package com.umg.sgau.colegiatura.service;

import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;

import java.util.List;

public interface ColegiaturaService {

    ColegiaturaEntity crear(ColegiaturaEntity colegiatura);

    ColegiaturaEntity obtenerPorId(Long id);

    List<ColegiaturaEntity> obtenerTodos();

    ColegiaturaEntity actualizar(Long id, ColegiaturaEntity colegiatura);

    void eliminar(Long id);
}