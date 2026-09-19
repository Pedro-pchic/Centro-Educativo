package com.umg.sgau.colegiatura.exception;

public class ColegiaturaDuplicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ColegiaturaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
