package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Inscripción enviada desde Servicios → Inscripción (POST /api/inscripciones).
 * idCurso solo viene si tipoInscripcion es "curso"; objetivo solo si es "rutina".
 */
public record InscripcionDTO(String nombre, String cedula, String email, String telefono,
                             String fechaNacimiento, String tipoInscripcion,
                             String idCurso, String objetivo, String password) {
}
