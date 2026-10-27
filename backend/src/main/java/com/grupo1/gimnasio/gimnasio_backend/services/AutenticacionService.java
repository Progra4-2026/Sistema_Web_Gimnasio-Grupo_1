package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.LoginDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Manejo de sesiones HTTP (HttpSession). Implementación: AutenticacionServiceImpl.
 *
 * Flujo: el login guarda el usuario en la HttpSession y el navegador recibe la cookie
 * JSESSIONID; en cada petición siguiente Spring recupera la misma sesión con esa cookie.
 */
public interface AutenticacionService {

    String ATRIBUTO_USUARIO = "usuario";

    /**
     * Valida las credenciales (probando cliente, instructor y administrador, en ese orden)
     * y crea la sesión, incluido el SecurityContext de Spring Security. 401 si son incorrectas.
     */
    UsuarioSesionDTO iniciarSesion(LoginDTO credenciales, HttpServletRequest peticion, HttpServletResponse respuesta);

    /** Invalida la sesión actual, si existe. */
    void cerrarSesion(HttpServletRequest peticion);

    /** Usuario de la sesión actual. 401 si no hay sesión iniciada. */
    UsuarioSesionDTO usuarioActual(HttpSession sesion);

    int sesionesActivas();
}
