package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.LoginDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.AutenticacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Sesiones HTTP — /api/auth
 *
 *   POST /api/auth/login    200 usuario + cookie JSESSIONID | 401 credenciales incorrectas
 *   GET  /api/auth/sesion   200 usuario en sesión          | 401 sin sesión
 *   POST /api/auth/logout   204 sesión cerrada
 *
 * El frontend debe hacer fetch con credentials: 'include' para que el navegador envíe la cookie.
 */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController extends ControladorBase {

    private final AutenticacionService autenticacionService;

    public AutenticacionController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO credenciales, HttpServletRequest peticion) {
        UsuarioSesionDTO usuario = autenticacionService.iniciarSesion(credenciales, peticion);
        return Map.of("mensaje", "Sesión iniciada. ¡Hola, " + usuario.nombre() + "!", "usuario", usuario);
    }

    @GetMapping("/sesion")
    public Map<String, Object> sesion(HttpSession sesion) {
        UsuarioSesionDTO usuario = autenticacionService.usuarioActual(sesion);
        return Map.of("usuario", usuario, "sesionesActivas", autenticacionService.sesionesActivas());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest peticion) {
        autenticacionService.cerrarSesion(peticion);
        return ResponseEntity.noContent().build();
    }
}
