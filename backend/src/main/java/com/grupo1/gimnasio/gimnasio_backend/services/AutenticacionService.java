package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.LoginDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Inicio y cierre de sesión para los tres roles (cliente, instructor y administrador).
 * Implementación: AutenticacionServiceImpl.
 *
 * Flujo: el login guarda el usuario y su rol en la HttpSession y en el SecurityContext de
 * Spring Security; el navegador recibe la cookie JSESSIONID y en cada petición siguiente
 * Spring recupera la misma sesión, con lo que @PreAuthorize sabe qué rol tiene.
 */
public interface AutenticacionService {

    String ATRIBUTO_USUARIO = "usuario";

    /** Valida las credenciales (cliente, instructor o administrador) y crea la sesión. 401 si son incorrectas. */
    UsuarioSesionDTO iniciarSesion(LoginDTO credenciales, HttpServletRequest peticion, HttpServletResponse respuesta);

    /** Invalida la sesión actual, si existe. */
    void cerrarSesion(HttpServletRequest peticion);

    /** Usuario de la sesión actual. 401 si no hay sesión iniciada. */
    UsuarioSesionDTO usuarioActual(HttpSession sesion);

    int sesionesActivas();
}
