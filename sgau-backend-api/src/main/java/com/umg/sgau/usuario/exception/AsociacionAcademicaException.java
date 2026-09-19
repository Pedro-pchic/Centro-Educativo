package com.umg.sgau.usuario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AsociacionAcademicaException extends RuntimeException {

    public AsociacionAcademicaException(String message) {
        super(message);
    }
}
