package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Consulta enviada desde el formulario de Contactos (POST /api/consultas). */
public record ConsultaDTO(
        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 3, max = 80, message = "Debe tener entre 3 y 80 caracteres.")
        String nombre,

        @NotBlank(message = "Este campo es obligatorio.")
        @Email(message = "Correo electrónico inválido.")
        @Size(max = 100, message = "Debe tener como máximo 100 caracteres.")
        String email,

        @NotBlank(message = "Este campo es obligatorio.")
        @Pattern(regexp = "^[0-9]{4}-?[0-9]{4}$", message = "El teléfono debe tener 8 dígitos.")
        String telefono,

        @Size(max = 20, message = "Asunto inválido.")
        String asunto,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 10, max = 500, message = "Debe tener entre 10 y 500 caracteres.")
        String mensaje) {
}
