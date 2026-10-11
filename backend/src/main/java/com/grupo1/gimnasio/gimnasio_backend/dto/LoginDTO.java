package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.NotBlank;

/** Credenciales para POST /api/auth/login. */
public record LoginDTO(
        @NotBlank(message = "Ingresá tu cédula.")
        String cedula,

        @NotBlank(message = "Ingresá tu contraseña.")
        String password) {
}
