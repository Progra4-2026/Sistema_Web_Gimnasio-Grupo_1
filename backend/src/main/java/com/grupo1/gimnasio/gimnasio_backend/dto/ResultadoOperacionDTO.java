package com.grupo1.gimnasio.gimnasio_backend.dto;

/** Respuesta de una actualización: mensaje, cantidad de registros afectados y el dato resultante. */
public record ResultadoOperacionDTO<T>(String mensaje, int registrosAfectados, T dato) {
}
