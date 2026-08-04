package com.umg.sgau.docente.exception;

public class DocenteNoEncontradoException extends RuntimeException { 
	
	public DocenteNoEncontradoException(Long id) { 
		super("Docente no encontrado con id: " + id); 
	} 
	
}
