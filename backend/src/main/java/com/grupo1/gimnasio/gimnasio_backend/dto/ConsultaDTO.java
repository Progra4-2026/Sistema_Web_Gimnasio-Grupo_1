package com.grupo1.gimnasio.gimnasio_backend.dto;

/** Consulta enviada desde el formulario de Contactos (POST /api/consultas). */
public record ConsultaDTO(String nombre, String email, String telefono, String asunto, String mensaje) {
}
