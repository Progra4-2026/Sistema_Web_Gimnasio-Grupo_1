package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.grupo1.gimnasio.gimnasio_backend.controllers.ValidacionBasica.*;

/**
 * POST /api/inscripciones — Recibe el formulario de Inscripción.
 * 201 Created, 400 Bad Request (datos inválidos) o 409 Conflict (cédula ya inscrita).
 * TEMPORAL (Entregable 3): las cédulas se guardan en memoria para demostrar el 409;
 * en el Entregable 4 se persiste con JPA y la contraseña se cifra con BCrypt.
 */
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final Set<String> cedulasInscritas = ConcurrentHashMap.newKeySet();

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody InscripcionDTO ins) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "nombre", ins.nombre());
        longitud(errores, "nombre", ins.nombre(), 3, 80);
        requerido(errores, "cedula", ins.cedula());
        patron(errores, "cedula", ins.cedula(), CEDULA, "La cédula debe tener entre 9 y 12 dígitos.");
        requerido(errores, "email", ins.email());
        patron(errores, "email", ins.email(), EMAIL, "Correo electrónico inválido.");
        requerido(errores, "telefono", ins.telefono());
        patron(errores, "telefono", ins.telefono(), TELEFONO, "El teléfono debe tener 8 dígitos.");
        requerido(errores, "fechaNacimiento", ins.fechaNacimiento());
        requerido(errores, "password", ins.password());
        if (!vacio(ins.password()) && ins.password().length() < 8) {
            errores.put("password", "La contraseña debe tener al menos 8 caracteres.");
        }
        if (vacio(ins.tipoInscripcion()) || !Set.of("rutina", "curso").contains(ins.tipoInscripcion())) {
            errores.put("tipoInscripcion", "Seleccioná rutina o curso.");
        } else if ("curso".equals(ins.tipoInscripcion())) {
            requerido(errores, "idCurso", ins.idCurso());
        } else {
            requerido(errores, "objetivo", ins.objetivo());
        }

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(cuerpoError("Algunos datos no son válidos.", errores));
        }

        String cedula = ins.cedula().trim();
        if (!cedulasInscritas.add(cedula)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(cuerpoError("Ya existe una inscripción con la cédula " + cedula + ".", null));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "cedula", cedula,
                "mensaje", "¡Bienvenido/a, " + ins.nombre().trim() + "! Tu inscripción a "
                        + ("curso".equals(ins.tipoInscripcion()) ? "un curso grupal" : "una rutina personalizada")
                        + " quedó registrada."));
    }
}
