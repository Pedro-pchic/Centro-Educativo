package com.umg.sgau.nota.exception;

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