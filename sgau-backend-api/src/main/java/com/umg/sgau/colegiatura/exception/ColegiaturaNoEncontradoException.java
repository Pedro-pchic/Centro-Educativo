package com.umg.sgau.colegiatura.exception;

public class ColegiaturaNoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ColegiaturaNoEncontradaException(Long id) {
        super("Colegiatura no encontrada con id: " + id);
    }
}