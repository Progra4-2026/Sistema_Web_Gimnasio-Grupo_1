package com.grupo1.gimnasio.gimnasio_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Inscripción enviada desde Servicios → Inscripción (POST /api/inscripciones).
 * idCurso solo viene si tipoInscripcion es "curso"; objetivo solo si es "rutina".
 * Las reglas del grupo AlCrear se validan solo en POST (en PUT no se envían cédula ni contraseña).
 */
public record InscripcionDTO(
        @NotBlank(message = "Este campo es obligatorio.")
        @Size(min = 3, max = 80, message = "Debe tener entre 3 y 80 caracteres.")
        String nombre,

        @NotBlank(groups = AlCrear.class, message = "Este campo es obligatorio.")
        @Pattern(regexp = "^[0-9]{9,12}$", message = "La cédula debe tener entre 9 y 12 dígitos.")
        String cedula,

        @NotBlank(message = "Este campo es obligatorio.")
        @Email(message = "Correo electrónico inválido.")
        @Size(max = 100, message = "Debe tener como máximo 100 caracteres.")
        String email,

        @NotBlank(message = "Este campo es obligatorio.")
        @Pattern(regexp = "^[0-9]{4}-?[0-9]{4}$", message = "El teléfono debe tener 8 dígitos.")
        String telefono,

        @NotBlank(message = "Este campo es obligatorio.")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Usá el formato AAAA-MM-DD.")
        String fechaNacimiento,

        @NotBlank(groups = AlCrear.class, message = "Seleccioná rutina o curso.")
        @Pattern(regexp = "rutina|curso", message = "Seleccioná rutina o curso.")
        String tipoInscripcion,

        @Pattern(regexp = "^[0-9]+$", message = "Seleccioná un curso válido.")
        String idCurso,

        @Pattern(regexp = "fuerza|resistencia|peso|flexibilidad", message = "Seleccioná un objetivo válido.")
        String objetivo,

        @NotBlank(groups = AlCrear.class, message = "Este campo es obligatorio.")
        @Size(min = 8, max = 100, groups = AlCrear.class, message = "La contraseña debe tener al menos 8 caracteres.")
        String password) {
}
