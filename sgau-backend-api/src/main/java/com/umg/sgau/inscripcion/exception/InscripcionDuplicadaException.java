package com.umg.sgau.inscripcion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InscripcionDuplicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InscripcionDuplicadaException(Long estudianteId, Long cursoId) {
        super("El estudiante con id " + estudianteId
                + " ya posee una inscripción activa en el curso con id " + cursoId);
    }
}
