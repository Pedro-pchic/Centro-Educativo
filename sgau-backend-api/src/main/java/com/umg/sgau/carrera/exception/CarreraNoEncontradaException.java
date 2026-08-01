package com.umg.sgau.carrera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarreraNoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CarreraNoEncontradaException(Long id) {
        super("No se encontró una carrera activa con el ID: " + id);
    }

    public CarreraNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
