package com.grupo1.gimnasio.gimnasio_backend.observer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PATRÓN OBSERVER — Subject.
 *
 * Mantiene la lista de observadores y les avisa cada vez que InscripcionServiceImpl
 * registra o elimina una inscripción. El servicio no conoce a los observadores:
 * solo llama a notificar(), así se pueden agregar reacciones nuevas sin tocarlo.
 *
 * Al arrancar, Spring entrega todos los beans que implementan ObservadorInscripciones
 * y cada uno queda suscrito.
 */
@Component
public class PublicadorInscripciones {

    private static final Logger LOG = Logger.getLogger(PublicadorInscripciones.class.getName());

    private final List<ObservadorInscripciones> observadores = new CopyOnWriteArrayList<>();

    public PublicadorInscripciones(List<ObservadorInscripciones> iniciales) {
        iniciales.forEach(this::suscribir);
    }

    public void suscribir(ObservadorInscripciones observador) {
        if (!observadores.contains(observador)) observadores.add(observador);
    }

    public void desuscribir(ObservadorInscripciones observador) {
        observadores.remove(observador);
    }

    public void notificar(EventoInscripcion evento) {
        for (ObservadorInscripciones observador : observadores) {
            try {
                observador.actualizar(evento);
            } catch (RuntimeException e) {
                // Un observador con error no debe impedir que los demás reciban el aviso
                LOG.log(Level.WARNING, "El observador " + observador.getClass().getSimpleName() + " falló", e);
            }
        }
    }
}
