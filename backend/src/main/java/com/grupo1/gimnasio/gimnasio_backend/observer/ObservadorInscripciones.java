package com.grupo1.gimnasio.gimnasio_backend.observer;

/**
 * PATRÓN OBSERVER — interfaz Observer.
 * Cada clase que la implementa reacciona cuando un cliente se inscribe o se desinscribe.
 */
public interface ObservadorInscripciones {

    void actualizar(EventoInscripcion evento);
}
