package com.grupo1.gimnasio.gimnasio_backend.observer;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/** PATRÓN OBSERVER — Observer concreto: registra cada inscripción y desinscripción en la bitácora. */
@Component
public class BitacoraInscripciones implements ObservadorInscripciones {

    private static final Logger LOG = Logger.getLogger(BitacoraInscripciones.class.getName());

    @Override
    public void actualizar(EventoInscripcion evento) {
        String detalle = "curso".equals(evento.tipoInscripcion())
                ? "curso #" + evento.idCurso()
                : evento.tipoInscripcion();
        LOG.info(() -> evento.tipo() + ": " + evento.nombre() + " (cédula " + evento.cedula() + "), " + detalle);
    }
}
