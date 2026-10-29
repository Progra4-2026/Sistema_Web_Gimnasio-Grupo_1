package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Máquina tal como la envía y recibe la API (GET/POST/PUT /api/maquinas).
 * El id lo asigna la base de datos; en PUT no se permite cambiarlo.
 */
public record MaquinaDTO(
        Long id,

        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 3, max = 50, message = "Debe tener entre 3 y 50 caracteres.")
        String descripcion,

        @Pattern(regexp = "excelente|bueno|regular|en reparación",
                message = "El estado debe ser excelente, bueno, regular o en reparación.")
        String estado) {
}
