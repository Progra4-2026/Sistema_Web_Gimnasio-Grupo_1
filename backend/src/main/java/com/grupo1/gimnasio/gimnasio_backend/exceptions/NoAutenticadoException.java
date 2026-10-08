package com.grupo1.gimnasio.gimnasio_backend.exceptions;

import org.springframework.http.HttpStatus;

/** 401 Unauthorized: no hay sesión iniciada o las credenciales son incorrectas. */
public class NoAutenticadoException extends ApiException {

    public NoAutenticadoException(String mensaje) {
        super(HttpStatus.UNAUTHORIZED, mensaje);
    }
}
