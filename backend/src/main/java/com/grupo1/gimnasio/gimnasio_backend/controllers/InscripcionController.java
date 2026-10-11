package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionRespuestaDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.AutenticacionService;
import com.grupo1.gimnasio.gimnasio_backend.services.InscripcionService;
import jakarta.servlet.http.HttpSession;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CRUD de inscripciones — /api/inscripciones
 *
 *   GET    /api/inscripciones            200 lista (sin contraseñas)
 *   GET    /api/inscripciones/mia        200 la del usuario en sesión | 401 sin sesión
 *   GET    /api/inscripciones/{cedula}   200 | 404
 *   POST   /api/inscripciones            201 + Location | 400 | 404 (curso) | 409 (cédula duplicada / sin cupos)
 *   PUT    /api/inscripciones/{cedula}   200 | 400 (intenta cambiar PK/FK) | 404
 *   DELETE /api/inscripciones/{cedula}   204 (desinscribirse) | 404
 */
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController extends ControladorBase {

    private final InscripcionService inscripcionService;
    private final AutenticacionService autenticacionService;

    public InscripcionController(InscripcionService inscripcionService, AutenticacionService autenticacionService) {
        this.inscripcionService = inscripcionService;
        this.autenticacionService = autenticacionService;
    }

    @GetMapping
    public List<InscripcionRespuestaDTO> listar() {
        return inscripcionService.listar();
    }

    /** Ejemplo de uso de la HttpSession: el cliente solo ve su propia inscripción. */
    @GetMapping("/mia")
    public InscripcionRespuestaDTO mia(HttpSession sesion) {
        String cedula = autenticacionService.usuarioActual(sesion).cedula();
        return inscripcionService.obtener(cedula);
    }

    @GetMapping("/{cedula}")
    public InscripcionRespuestaDTO obtener(@PathVariable String cedula) {
        return inscripcionService.obtener(cedula);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody InscripcionDTO datos) {
        InscripcionRespuestaDTO creada = inscripcionService.crear(datos);
        String detalle = "curso".equals(creada.tipoInscripcion()) ? "un curso grupal" : "una rutina personalizada";

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", "¡Bienvenido/a, " + creada.nombre() + "! Tu inscripción a " + detalle + " quedó registrada.");
        cuerpo.put("inscripcion", creada);
        return ResponseEntity.created(URI.create("/api/inscripciones/" + creada.cedula())).body(cuerpo);
    }

    @PutMapping("/{cedula}")
    public ResultadoOperacionDTO<InscripcionRespuestaDTO> actualizar(@PathVariable String cedula,
                                                                     @RequestBody InscripcionDTO cambios) {
        InscripcionRespuestaDTO actualizada = inscripcionService.actualizar(cedula, cambios);
        return new ResultadoOperacionDTO<>("Inscripción actualizada correctamente.", 1, actualizada);
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminar(@PathVariable String cedula) {
        inscripcionService.eliminar(cedula);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
