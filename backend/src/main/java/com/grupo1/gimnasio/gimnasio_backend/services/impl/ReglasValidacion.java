package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Reglas de validación compartidas por los servicios (capa de lógica de negocio).
 * Complementan las anotaciones de Bean Validation (@Valid, @NotBlank, @Email...) de los DTOs.
 */
final class ReglasValidacion {

    static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    static final Pattern TELEFONO = Pattern.compile("^[0-9]{4}-?[0-9]{4}$");
    static final Pattern CEDULA = Pattern.compile("^[0-9]{9,12}$");

    private ReglasValidacion() {
    }

    static boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    static String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    static void requerido(Map<String, String> errores, String campo, String valor) {
        if (vacio(valor)) errores.putIfAbsent(campo, "Este campo es obligatorio.");
    }

    static void longitud(Map<String, String> errores, String campo, String valor, int min, int max) {
        if (!vacio(valor)) {
            int largo = valor.trim().length();
            if (largo < min || largo > max) {
                errores.putIfAbsent(campo, "Debe tener entre " + min + " y " + max + " caracteres.");
            }
        }
    }

    static void patron(Map<String, String> errores, String campo, String valor, Pattern patron, String mensaje) {
        if (!vacio(valor) && !patron.matcher(valor.trim()).matches()) errores.putIfAbsent(campo, mensaje);
    }
}
