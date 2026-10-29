package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Credenciales para POST /api/auth/login.
 * "cedula" es el identificador de usuario: cédula (cliente) o código (instructor o administrador).
 */
public record LoginDTO(
        @NotBlank(message = "Ingresá tu usuario (cédula o código).")
        String cedula,

        @NotBlank(message = "Ingresá tu contraseña.")
        String password) {
}
