package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.InstructorDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.InstructorService;
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
 * Gestión de instructores — /api/admin/instructores (solo ADMINISTRADOR)
 *
 *   GET    /api/admin/instructores          200 lista (sin contraseñas)
 *   GET    /api/admin/instructores/{cod}    200 | 404
 *   POST   /api/admin/instructores          201 + Location | 400
 *   PUT    /api/admin/instructores/{cod}    200 | 400 | 404
 *   DELETE /api/admin/instructores/{cod}    204 | 404 | 409 (tiene rutinas o historial)
 */
@RestController
@RequestMapping("/api/admin/instructores")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminInstructorController {

    private final InstructorService instructorService;

    public AdminInstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public List<InstructorDTO> listar() {
        return instructorService.listar();
    }

    @GetMapping("/{codInstructor}")
    public InstructorDTO obtener(@PathVariable long codInstructor) {
        return instructorService.obtener(codInstructor);
    }

    @PostMapping
    public ResponseEntity<InstructorDTO> crear(@Valid @RequestBody InstructorDTO datos) {
        InstructorDTO creado = instructorService.crear(datos);
        return ResponseEntity.created(URI.create("/api/admin/instructores/" + creado.codInstructor())).body(creado);
    }

    @PutMapping("/{codInstructor}")
    public ResultadoOperacionDTO<InstructorDTO> actualizar(@PathVariable long codInstructor,
                                                           @Valid @RequestBody InstructorDTO cambios) {
        return new ResultadoOperacionDTO<>("Instructor actualizado correctamente.", 1,
                instructorService.actualizar(codInstructor, cambios));
    }

    @DeleteMapping("/{codInstructor}")
    public ResponseEntity<Void> eliminar(@PathVariable long codInstructor) {
        instructorService.eliminar(codInstructor);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
