package com.grupo1.gimnasio.gimnasio_backend.dto;

/** Datos de un curso tal como los consume el frontend (GET /api/cursos). */
public record CursoDTO(int id, String descripcion, String detalle, String horario, int cupos, String imagen) {
}
