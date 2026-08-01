package com.umg.sgau.usuario.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public UsuarioNoEncontradoException(Long id) {
        super("Usuario no encontrado con id: " + id);
    }
}


