package com.grupo1.gimnasio.gimnasio_backend.sesion;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

/**
 * Cuando una sesión vence por inactividad (o se invalida), el servidor llama a sessionDestroyed
 * y se quita del registro único GestorSesiones. Spring Boot registra este listener automáticamente.
 */
@Component
public class ListenerSesiones implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent evento) {
        GestorSesiones.getInstancia().eliminar(evento.getSession().getId());
    }
}
