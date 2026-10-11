package com.grupo1.gimnasio.gimnasio_backend.observer;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PATRÓN OBSERVER — Observer concreto: lleva el conteo de inscripciones desde que arrancó el servidor.
 * Se consulta en GET /api/inscripciones/estadisticas.
 */
@Component
public class EstadisticasInscripciones implements ObservadorInscripciones {

    private final AtomicInteger inscripciones = new AtomicInteger();
    private final AtomicInteger desinscripciones = new AtomicInteger();
    private final AtomicInteger aCursos = new AtomicInteger();
    private final AtomicInteger aRutinas = new AtomicInteger();
    private volatile LocalDateTime ultimoCambio;

    @Override
    public void actualizar(EventoInscripcion evento) {
        if (evento.tipo() == EventoInscripcion.Tipo.INSCRIPCION) {
            inscripciones.incrementAndGet();
            if ("curso".equals(evento.tipoInscripcion())) aCursos.incrementAndGet();
            else aRutinas.incrementAndGet();
        } else {
            desinscripciones.incrementAndGet();
        }
        ultimoCambio = evento.fecha();
    }

    public Map<String, Object> resumen() {
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("inscripciones", inscripciones.get());
        resumen.put("desinscripciones", desinscripciones.get());
        resumen.put("aCursos", aCursos.get());
        resumen.put("aRutinas", aRutinas.get());
        resumen.put("ultimoCambio", ultimoCambio == null ? null : ultimoCambio.withNano(0).toString());
        return resumen;
    }
}
