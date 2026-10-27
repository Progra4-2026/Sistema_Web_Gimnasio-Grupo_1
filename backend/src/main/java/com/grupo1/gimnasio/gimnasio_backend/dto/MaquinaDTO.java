package com.grupo1.gimnasio.gimnasio_backend.dto;

/** Máquina tal como la envía y recibe la API (GET/POST/PUT /api/maquinas). */
public record MaquinaDTO(Integer id, String descripcion, String estado) {
}
