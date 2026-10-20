package com.grupo1.gimnasio.gimnasio_backend.observer;

import java.time.LocalDateTime;

/**
 * Lo que el Subject (PublicadorInscripciones) envía a cada Observer:
 * qué pasó, con qué cliente y a qué se inscribió.
 */
public record EventoInscripcion(Tipo tipo, String cedula, String nombre, String tipoInscripcion,
                                Integer idCurso, LocalDateTime fecha) {

    public enum Tipo { INSCRIPCION, DESINSCRIPCION }
}
