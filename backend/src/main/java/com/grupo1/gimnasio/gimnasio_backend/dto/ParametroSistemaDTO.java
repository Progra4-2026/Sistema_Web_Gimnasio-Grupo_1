package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Parámetro del sistema (clave/valor) que gestiona el administrador. Solo se modifica el valor. */
public record ParametroSistemaDTO(
        String clave,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(max = 255, message = "Debe tener como máximo 255 caracteres.")
        String valor,

        String descripcion) {
}
