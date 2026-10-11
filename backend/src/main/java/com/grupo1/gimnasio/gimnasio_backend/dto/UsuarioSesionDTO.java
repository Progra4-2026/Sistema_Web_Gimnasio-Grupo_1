package com.grupo1.gimnasio.gimnasio_backend.dto;

/** Usuario autenticado que se guarda en la HttpSession. */
public record UsuarioSesionDTO(String cedula, String nombre, String rol) {
}
