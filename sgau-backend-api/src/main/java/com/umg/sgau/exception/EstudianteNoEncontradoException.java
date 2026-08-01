package com.umg.sgau.exception;

public class EstudianteNoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EstudianteNoEncontradoException(Long id) {
        super("Estudiante no encontrado con id: " + id);
    }
}
