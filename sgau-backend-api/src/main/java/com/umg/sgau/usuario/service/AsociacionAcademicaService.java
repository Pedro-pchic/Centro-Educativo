package com.umg.sgau.usuario.service;

public interface AsociacionAcademicaService {

    void asociarEstudiante(Long usuarioId, Long estudianteId);

    void asociarDocente(Long usuarioId, Long docenteId);
}
