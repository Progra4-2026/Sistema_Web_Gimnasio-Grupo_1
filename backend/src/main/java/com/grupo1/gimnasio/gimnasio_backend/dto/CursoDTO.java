package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Curso tal como lo envía y recibe la API (GET/POST/PUT /api/cursos).
 * En POST el id lo asigna el servidor; en PUT no se permite cambiarlo.
 */
public record CursoDTO(Integer id, String descripcion, String detalle, String horario,
                       Integer cupos, String imagen) {
}
