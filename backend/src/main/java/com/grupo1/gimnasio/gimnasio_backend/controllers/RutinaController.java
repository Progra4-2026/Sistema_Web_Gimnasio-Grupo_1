package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.RutinaDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.RutinaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Rutinas — /api/rutinas (las gestiona el instructor; el cliente solo ve las suyas).
 *
 *   GET    /api/rutinas         200 todas                                          — INSTRUCTOR
 *   GET    /api/rutinas/mias    200 solo las del cliente en sesión                 — CLIENTE
 *   GET    /api/rutinas/{id}    200 | 404                                          — INSTRUCTOR
 *   POST   /api/rutinas         201 + Location | 400 (instructor = el de la sesión) — INSTRUCTOR
 *   PUT    /api/rutinas/{id}    200 | 400 (intenta cambiar FK) | 404               — INSTRUCTOR
 *   DELETE /api/rutinas/{id}    204 | 404                                          — INSTRUCTOR
 *
 * La cédula del cliente y el código del instructor se leen de la sesión (authentication.name),
 * nunca de lo que envía el navegador.
 */
@RestController
@RequestMapping("/api/rutinas")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class RutinaController {

    private final RutinaService rutinaService;

    public RutinaController(RutinaService rutinaService) {
        this.rutinaService = rutinaService;
    }

    @GetMapping
    public List<RutinaDTO> listar() {
        return rutinaService.listar();
    }

    /** La anotación del método reemplaza a la de la clase: aquí solo entra el rol CLIENTE. */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mias")
    public List<RutinaDTO> mias(Authentication autenticacion) {
        return rutinaService.listarDeCliente(Long.parseLong(autenticacion.getName()));
    }

    @GetMapping("/{id}")
    public RutinaDTO obtener(@PathVariable long id) {
        return rutinaService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<RutinaDTO> crear(@Valid @RequestBody RutinaDTO rutina, Authentication autenticacion) {
        RutinaDTO creada = rutinaService.crear(rutina, Long.parseLong(autenticacion.getName()));
        return ResponseEntity.created(URI.create("/api/rutinas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public ResultadoOperacionDTO<RutinaDTO> actualizar(@PathVariable long id, @Valid @RequestBody RutinaDTO cambios) {
        return new ResultadoOperacionDTO<>("Rutina actualizada correctamente.", 1, rutinaService.actualizar(id, cambios));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        rutinaService.eliminar(id);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
