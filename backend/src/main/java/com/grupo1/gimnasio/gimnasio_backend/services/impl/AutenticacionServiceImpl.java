package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.LoginDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.NoAutenticadoException;
import com.grupo1.gimnasio.gimnasio_backend.services.AutenticacionService;
import com.grupo1.gimnasio.gimnasio_backend.services.InscripcionService;
import com.grupo1.gimnasio.gimnasio_backend.sesion.GestorSesiones;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Manejo de sesiones con HttpSession:
 *  - login: valida credenciales, renueva el id de sesión (evita fijación de sesión),
 *    guarda el usuario en la sesión y la registra en el Singleton GestorSesiones;
 *  - la sesión expira tras 30 minutos sin actividad;
 *  - logout: invalida la sesión y la quita del registro.
 *
 * Patrón Singleton: bean de alcance singleton + uso de GestorSesiones.getInstancia().
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AutenticacionServiceImpl implements AutenticacionService {

    private static final int MINUTOS_INACTIVIDAD = 30;

    private final InscripcionService inscripcionService;
    private final GestorSesiones gestorSesiones = GestorSesiones.getInstancia();

    public AutenticacionServiceImpl(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @Override
    public UsuarioSesionDTO iniciarSesion(LoginDTO credenciales, HttpServletRequest peticion) {
        if (credenciales == null) throw new NoAutenticadoException("Ingresá tu cédula y contraseña.");
        UsuarioSesionDTO usuario = inscripcionService.autenticar(credenciales.cedula(), credenciales.password());

        // Si ya había una sesión, se quita del registro antes de renovar su id
        HttpSession anterior = peticion.getSession(false);
        if (anterior != null) gestorSesiones.eliminar(anterior.getId());

        HttpSession sesion = peticion.getSession(true);
        peticion.changeSessionId();
        sesion.setAttribute(ATRIBUTO_USUARIO, usuario);
        sesion.setMaxInactiveInterval(MINUTOS_INACTIVIDAD * 60);
        gestorSesiones.registrar(sesion.getId(), usuario);
        return usuario;
    }

    @Override
    public void cerrarSesion(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            gestorSesiones.eliminar(sesion.getId());
            sesion.invalidate();
        }
    }

    @Override
    public UsuarioSesionDTO usuarioActual(HttpSession sesion) {
        Object usuario = sesion == null ? null : sesion.getAttribute(ATRIBUTO_USUARIO);
        if (usuario instanceof UsuarioSesionDTO u) return u;
        throw new NoAutenticadoException("Iniciá sesión para continuar.");
    }

    @Override
    public int sesionesActivas() {
        return gestorSesiones.cantidadActivas();
    }
}
