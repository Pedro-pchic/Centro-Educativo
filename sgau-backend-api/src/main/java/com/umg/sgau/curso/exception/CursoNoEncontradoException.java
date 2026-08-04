package com.umg.sgau.curso.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CursoNoEncontradoException(Long id) {
        super("No se encontró un curso activo con el ID: " + id);
    }

    public CursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

