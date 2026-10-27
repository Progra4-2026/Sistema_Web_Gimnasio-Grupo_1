package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.InstructorDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.InstructorService;
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

/** Gestión de instructores por el administrador — /api/admin/instructores. */
@RestController
@RequestMapping("/api/admin/instructores")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminInstructorController extends ControladorBase {

    private final InstructorService instructorService;

    public AdminInstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public List<InstructorDTO> listar() {
        return instructorService.listar();
    }

    @GetMapping("/{codInstructor}")
    public InstructorDTO obtener(@PathVariable int codInstructor) {
        return instructorService.obtener(codInstructor);
    }

    @PostMapping
    public ResponseEntity<InstructorDTO> crear(@RequestBody InstructorDTO instructor) {
        InstructorDTO creado = instructorService.crear(instructor);
        return ResponseEntity.created(URI.create("/api/admin/instructores/" + creado.codInstructor())).body(creado);
    }

    @PutMapping("/{codInstructor}")
    public ResultadoOperacionDTO<InstructorDTO> actualizar(@PathVariable int codInstructor,
                                                            @RequestBody InstructorDTO cambios) {
        InstructorDTO actualizado = instructorService.actualizar(codInstructor, cambios);
        return new ResultadoOperacionDTO<>("Instructor actualizado correctamente.", 1, actualizado);
    }

    @DeleteMapping("/{codInstructor}")
    public ResponseEntity<Void> eliminar(@PathVariable int codInstructor) {
        instructorService.eliminar(codInstructor);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
