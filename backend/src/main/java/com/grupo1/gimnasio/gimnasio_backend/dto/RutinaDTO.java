package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Rutina tal como la envía y recibe la API (GET/POST/PUT /api/rutinas).
 * "instructor" lo fija siempre el servidor con el instructor autenticado, nunca el cliente.
 */
public record RutinaDTO(Integer id, Integer cliente, Integer instructor, Integer maquina,
                        String fecha, Integer horas) {
}
