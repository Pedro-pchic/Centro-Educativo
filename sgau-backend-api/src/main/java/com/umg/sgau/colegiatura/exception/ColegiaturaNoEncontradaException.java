package com.umg.sgau.colegiatura.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ColegiaturaNoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ColegiaturaNoEncontradaException(Long id) {
        super("Colegiatura no encontrada con id: " + id);
    }
}
