package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Administrador tal como lo envía y recibe la API (/api/admin/administradores).
 * El código lo asigna la base de datos y es el usuario para iniciar sesión.
 * password: obligatoria al crear; nunca se devuelve en la respuesta.
 */
public record AdministradorDTO(
        Long codAdministrador,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 2, max = 60, message = "Debe tener entre 2 y 60 caracteres.")
        String nombre,

        @Email(message = "Correo electrónico inválido.")
        @Size(max = 100, message = "Debe tener como máximo 100 caracteres.")
        String email,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres.")
        String password) {
}
