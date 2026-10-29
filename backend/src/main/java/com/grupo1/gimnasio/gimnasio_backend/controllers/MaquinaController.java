package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.MaquinaDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.MaquinaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * CRUD de máquinas — /api/maquinas
 *
 *   GET    /api/maquinas        200 lista                          — público
 *   GET    /api/maquinas/{id}   200 | 404                          — público
 *   POST   /api/maquinas        201 + Location | 400               — INSTRUCTOR
 *   PUT    /api/maquinas/{id}   200 | 400 | 404                    — INSTRUCTOR
 *   DELETE /api/maquinas/{id}   204 | 404 | 409 (usada en rutinas) — INSTRUCTOR
 */
@RestController
@RequestMapping("/api/maquinas")
public class MaquinaController {

    private final MaquinaService maquinaService;

    public MaquinaController(MaquinaService maquinaService) {
        this.maquinaService = maquinaService;
    }

    @GetMapping
    public List<MaquinaDTO> listar() {
        return maquinaService.listar();
    }

    @GetMapping("/{id}")
    public MaquinaDTO obtener(@PathVariable long id) {
        return maquinaService.obtener(id);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<MaquinaDTO> crear(@Valid @RequestBody MaquinaDTO maquina) {
        MaquinaDTO creada = maquinaService.crear(maquina);
        return ResponseEntity.created(URI.create("/api/maquinas/" + creada.id())).body(creada);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResultadoOperacionDTO<MaquinaDTO> actualizar(@PathVariable long id, @Valid @RequestBody MaquinaDTO maquina) {
        return new ResultadoOperacionDTO<>("Máquina actualizada correctamente.", 1, maquinaService.actualizar(id, maquina));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        maquinaService.eliminar(id);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
