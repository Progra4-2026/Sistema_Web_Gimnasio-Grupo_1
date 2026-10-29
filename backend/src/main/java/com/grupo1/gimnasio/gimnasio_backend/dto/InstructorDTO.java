package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Instructor tal como lo envía y recibe la API (lo gestiona el administrador en /api/admin/instructores).
 * El código lo asigna la base de datos y es el usuario para iniciar sesión.
 * password: obligatoria al crear (validada en el servicio), opcional al actualizar; nunca se devuelve.
 */
public record InstructorDTO(
        Long codInstructor,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 2, max = 30, message = "Debe tener entre 2 y 30 caracteres.")
        String nombre,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 2, max = 30, message = "Debe tener entre 2 y 30 caracteres.")
        String apellido1,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 2, max = 30, message = "Debe tener entre 2 y 30 caracteres.")
        String apellido2,

        @Size(max = 50, message = "Debe tener como máximo 50 caracteres.")
        String direccion,

        @Email(message = "Correo electrónico inválido.")
        @Size(max = 30, message = "Debe tener como máximo 30 caracteres.")
        String email,

        @NotNull(message = "Este campo es obligatorio.")
        @Min(value = 10000000, message = "El teléfono debe tener 8 dígitos.")
        @Max(value = 99999999, message = "El teléfono debe tener 8 dígitos.")
        Integer telCel,

        @NotBlank(message = "Este campo es obligatorio.")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Usá el formato AAAA-MM-DD.")
        String fechaContratacion,

        @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres.")
        String password) {
}
