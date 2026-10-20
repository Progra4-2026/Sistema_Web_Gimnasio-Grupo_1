package com.grupo1.gimnasio.gimnasio_backend.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

/** 400 Bad Request: uno o más campos no cumplen las reglas. Incluye el error de cada campo. */
public class DatosInvalidosException extends ApiException {

    private final Map<String, String> errores;

    public DatosInvalidosException(Map<String, String> errores) {
        super(HttpStatus.BAD_REQUEST, "Algunos datos no son válidos.");
        this.errores = Map.copyOf(errores);
    }

    public DatosInvalidosException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, mensaje);
        this.errores = Map.of();
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
