package com.umg.sgau.carrera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CarreraDuplicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CarreraDuplicadaException(String codigo) {
        super("Ya existe una carrera registrada con el código: " + codigo);
    }
}
