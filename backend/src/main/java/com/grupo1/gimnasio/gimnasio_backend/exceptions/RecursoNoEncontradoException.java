package com.grupo1.gimnasio.gimnasio_backend.exceptions;

import org.springframework.http.HttpStatus;

/** 404 Not Found: el registro solicitado no existe. */
public class RecursoNoEncontradoException extends ApiException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(HttpStatus.NOT_FOUND, "No existe " + recurso + " con identificador " + id + ".");
    }
}
