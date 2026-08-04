package com.umg.sgau.curso.exception;

public class DocenteInactivoParaCursoException
        extends RuntimeException {

    public DocenteInactivoParaCursoException(Long docenteId) {
        super(
            "No se puede asignar el docente con ID "
            + docenteId
            + " porque se encuentra inactivo"
        );
    }

    public DocenteInactivoParaCursoException(String mensaje) {
        super(mensaje);
    }
}