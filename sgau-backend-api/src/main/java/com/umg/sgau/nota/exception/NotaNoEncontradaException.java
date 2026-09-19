package com.umg.sgau.nota.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotaNoEncontradaException extends RuntimeException {
    
    // Constructor para cuando buscamos por un ID específico
    public NotaNoEncontradaException(Long id) {
        super("No se encontró la calificación con el ID: " + id);
    }

    // Constructor para cuando queremos enviar un mensaje más detallado
    public NotaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
