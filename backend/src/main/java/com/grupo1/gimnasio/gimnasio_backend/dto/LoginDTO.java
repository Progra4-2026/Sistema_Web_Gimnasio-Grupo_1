package com.grupo1.gimnasio.gimnasio_backend.dto;

/** Credenciales para POST /api/auth/login. */
public record LoginDTO(String cedula, String password) {
}
