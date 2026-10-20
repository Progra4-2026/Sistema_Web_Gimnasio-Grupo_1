package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.ConsultaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.grupo1.gimnasio.gimnasio_backend.controllers.ValidacionBasica.*;

/**
 * POST /api/consultas — Recibe el formulario de Contactos.
 * 201 Created si es válido, 400 Bad Request con los errores por campo si no.
 * TEMPORAL (Entregable 3): solo registra la consulta en la bitácora; se guardará en BD en el Entregable 4.
 */
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private static final Logger LOG = Logger.getLogger(ConsultaController.class.getName());
    private final AtomicLong secuencia = new AtomicLong();

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody ConsultaDTO consulta) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "nombre", consulta.nombre());
        longitud(errores, "nombre", consulta.nombre(), 3, 80);
        requerido(errores, "email", consulta.email());
        patron(errores, "email", consulta.email(), EMAIL, "Correo electrónico inválido.");
        requerido(errores, "telefono", consulta.telefono());
        patron(errores, "telefono", consulta.telefono(), TELEFONO, "El teléfono debe tener 8 dígitos.");
        requerido(errores, "mensaje", consulta.mensaje());
        longitud(errores, "mensaje", consulta.mensaje(), 10, 500);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(cuerpoError("Algunos datos no son válidos.", errores));
        }

        long id = secuencia.incrementAndGet();
        LOG.info(() -> "Consulta #" + id + " recibida de " + consulta.email() + " (asunto: " + consulta.asunto() + ")");

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", id,
                "mensaje", "¡Gracias, " + consulta.nombre().trim() + "! Recibimos tu consulta y te responderemos pronto."));
    }
}
