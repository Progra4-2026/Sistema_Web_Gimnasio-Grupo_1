package com.grupo1.gimnasio.gimnasio_backend.dto;

/** Inscripción enviada desde Servicios → Inscripción (POST /api/inscripciones). */
public record InscripcionDTO(String nombre, String cedula, String email, String telefono,
                             String fechaNacimiento, String tipoInscripcion, String password) {
}
