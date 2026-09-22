package com.umg.sgau.usuario.service;

import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.usuario.entity.UsuarioEntity;

public interface IdentidadAcademicaService {

    UsuarioEntity obtenerUsuarioAutenticado();

    EstudianteEntity obtenerEstudianteAutenticado();

    DocenteEntity obtenerDocenteAutenticado();

    boolean esAdmin();
}
