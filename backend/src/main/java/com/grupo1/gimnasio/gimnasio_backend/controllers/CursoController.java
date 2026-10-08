package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.CursoDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.CursoService;
import org.springframework.http.ResponseEntity;
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
 * CRUD de cursos — /api/cursos  (Controlador del patrón MVC: recibe la petición,
 * delega en CursoService y devuelve la Vista en JSON).
 *
 *   GET    /api/cursos        200 lista
 *   GET    /api/cursos/{id}   200 | 404
 *   POST   /api/cursos        201 + Location | 400 | 409 (nombre duplicado)
 *   PUT    /api/cursos/{id}   200 | 400 | 404 | 409
 *   DELETE /api/cursos/{id}   204 | 404 | 409 (tiene clientes inscritos)
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController extends ControladorBase {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public List<CursoDTO> listar() {
        return cursoService.listar();
    }

    @GetMapping("/{id}")
    public CursoDTO obtener(@PathVariable int id) {
        return cursoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<CursoDTO> crear(@RequestBody CursoDTO curso) {
        CursoDTO creado = cursoService.crear(curso);
        return ResponseEntity.created(URI.create("/api/cursos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ResultadoOperacionDTO<CursoDTO> actualizar(@PathVariable int id, @RequestBody CursoDTO curso) {
        CursoDTO actualizado = cursoService.actualizar(id, curso);
        return new ResultadoOperacionDTO<>("Curso actualizado correctamente.", 1, actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
