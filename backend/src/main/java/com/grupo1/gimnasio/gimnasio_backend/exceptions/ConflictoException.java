package com.grupo1.gimnasio.gimnasio_backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * 409 Conflict: registro duplicado, o una operación que choca con datos relacionados
 * (por ejemplo, borrar un curso que tiene clientes inscritos).
 */
public class ConflictoException extends ApiException {

    public ConflictoException(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
