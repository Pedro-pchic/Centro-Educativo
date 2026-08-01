package com.umg.sgau.inscripcion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InscripcionNoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InscripcionNoEncontradaException(Long id) {
        super("No se encontró la inscripción con id: " + id);
    }
}
