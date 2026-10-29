package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Rutina tal como la envía y recibe la API (/api/rutinas).
 * El instructor nunca se toma del cuerpo de la petición: lo fija el servidor con el instructor
 * en sesión. Los nombres (clienteNombre, instructorNombre, maquinaDescripcion) solo van en la
 * respuesta, para mostrarlos sin otra consulta.
 */
public record RutinaDTO(
        Long id,

        @NotNull(message = "Seleccioná un cliente.")
        Long cliente,

        Long instructor,

        @NotNull(message = "Seleccioná una máquina.")
        Long maquina,

        @NotBlank(message = "Este campo es obligatorio.")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Usá el formato AAAA-MM-DD.")
        String fecha,

        @NotNull(message = "Este campo es obligatorio.")
        @Min(value = 1, message = "Las horas deben estar entre 1 y 24.")
        @Max(value = 24, message = "Las horas deben estar entre 1 y 24.")
        Integer horas,

        String clienteNombre,
        String instructorNombre,
        String maquinaDescripcion) {
}
