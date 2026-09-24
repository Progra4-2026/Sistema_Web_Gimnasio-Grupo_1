package com.grupo1.gimnasio.gimnasio_backend.controllers;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validaciones mínimas del lado del servidor para los endpoints del Entregable 3.
 * En el Entregable 4 se reemplazan por Bean Validation (@Valid, @NotBlank, @Email, @Pattern, @Size)
 * y un @ControllerAdvice central.
 */
final class ValidacionBasica {

    static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    static final Pattern TELEFONO = Pattern.compile("^[0-9]{4}-?[0-9]{4}$");
    static final Pattern CEDULA = Pattern.compile("^[0-9]{9,12}$");

    private ValidacionBasica() {
    }

    static boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    static void requerido(Map<String, String> errores, String campo, String valor) {
        if (vacio(valor)) errores.put(campo, "Este campo es obligatorio.");
    }

    static void longitud(Map<String, String> errores, String campo, String valor, int min, int max) {
        if (!vacio(valor) && (valor.trim().length() < min || valor.trim().length() > max)) {
            errores.put(campo, "Debe tener entre " + min + " y " + max + " caracteres.");
        }
    }

    static void patron(Map<String, String> errores, String campo, String valor, Pattern patron, String mensaje) {
        if (!vacio(valor) && !patron.matcher(valor.trim()).matches()) errores.put(campo, mensaje);
    }

    static Map<String, Object> cuerpoError(String mensaje, Map<String, String> errores) {
        return errores == null
                ? Map.of("mensaje", mensaje)
                : Map.of("mensaje", mensaje, "errores", errores);
    }
}
