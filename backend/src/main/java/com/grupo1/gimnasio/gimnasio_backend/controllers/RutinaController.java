package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.RutinaDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.RutinaService;
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
 * CRUD de rutinas — /api/rutinas (gestionadas por el instructor; el cliente solo ve las suyas).
 *
 *   GET    /api/rutinas         200 lista completa — solo INSTRUCTOR
 *   GET    /api/rutinas/mias    200 solo las del cliente en sesión — solo CLIENTE
 *   GET    /api/rutinas/{id}    200 | 404 — solo INSTRUCTOR
 *   POST   /api/rutinas         201 + Location | 400 — solo INSTRUCTOR (instructor = el autenticado)
 *   PUT    /api/rutinas/{id}    200 | 400 | 404 — solo INSTRUCTOR
 *   DELETE /api/rutinas/{id}    204 | 404 — solo INSTRUCTOR
 */
@RestController
@RequestMapping("/api/rutinas")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class RutinaController extends ControladorBase {

    private final RutinaService rutinaService;

    public RutinaController(RutinaService rutinaService) {
        this.rutinaService = rutinaService;
    }

    @GetMapping
    public List<RutinaDTO> listar() {
        return rutinaService.listar();
    }

    /** El cliente nunca pasa su propia cédula: se lee del SecurityContext (sesión autenticada). */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mias")
    public List<RutinaDTO> mias(Authentication autenticacion) {
        return rutinaService.listarDeCliente(Integer.parseInt(autenticacion.getName()));
    }

    @GetMapping("/{id}")
    public RutinaDTO obtener(@PathVariable int id) {
        return rutinaService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<RutinaDTO> crear(@RequestBody RutinaDTO rutina, Authentication autenticacion) {
        RutinaDTO creada = rutinaService.crear(rutina, Integer.parseInt(autenticacion.getName()));
        return ResponseEntity.created(URI.create("/api/rutinas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public ResultadoOperacionDTO<RutinaDTO> actualizar(@PathVariable int id, @RequestBody RutinaDTO cambios) {
        RutinaDTO actualizada = rutinaService.actualizar(id, cambios);
        return new ResultadoOperacionDTO<>("Rutina actualizada correctamente.", 1, actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        rutinaService.eliminar(id);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
