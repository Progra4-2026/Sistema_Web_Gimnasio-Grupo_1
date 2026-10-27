package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Administrador tal como lo envía y recibe la API. password es opcional al crear
 * (por defecto el propio código, hasheado); nunca se devuelve en la respuesta.
 */
public record AdministradorDTO(Integer codAdministrador, String nombre, String email, String password) {
}
