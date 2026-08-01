package com.umg.sgau.exception;

public class EstudianteNoEncontradoException extends RuntimeException {

	public EstudianteNoEncontradoException(Long id) {
        super("Estudiante no encontrado con id: " + id);
    }
}
