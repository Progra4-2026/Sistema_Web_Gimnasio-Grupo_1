package com.grupo1.gimnasio.gimnasio_backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Base de las excepciones de negocio. Cada una sabe qué código HTTP le corresponde,
 * así los servicios no dependen de ResponseEntity y los controladores no repiten try/catch.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus estado;

    protected ApiException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}
