package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Curso tal como lo envía y recibe la API (GET/POST/PUT /api/cursos).
 * En POST el id lo asigna la base de datos; en PUT no se permite cambiarlo.
 */
public record CursoDTO(
        Integer id,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 3, max = 50, message = "Debe tener entre 3 y 50 caracteres.")
        String descripcion,

        @Size(max = 300, message = "Debe tener como máximo 300 caracteres.")
        String detalle,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 3, max = 60, message = "Debe tener entre 3 y 60 caracteres.")
        String horario,

        @NotNull(message = "Este campo es obligatorio.")
        @Min(value = 0, message = "Los cupos deben estar entre 0 y 100.")
        @Max(value = 100, message = "Los cupos deben estar entre 0 y 100.")
        Integer cupos,

        @Pattern(regexp = "^[\\w-]+\\.(jpg|jpeg|png|webp)$",
                message = "La imagen debe ser un archivo .jpg, .jpeg, .png o .webp.")
        String imagen) {
}
