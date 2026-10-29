package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.AlCrear;
import com.grupo1.gimnasio.gimnasio_backend.observer.EstadisticasInscripciones;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;
import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionRespuestaDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.AutenticacionService;
import com.grupo1.gimnasio.gimnasio_backend.services.InscripcionService;
import jakarta.servlet.http.HttpSession;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CRUD de inscripciones — /api/inscripciones
 *
 *   GET    /api/inscripciones            200 lista (sin contraseñas)                       — ADMINISTRADOR
 *   GET    /api/inscripciones/mia        200 la del cliente en sesión                      — CLIENTE
 *   GET    /api/inscripciones/estadisticas  200 conteo que lleva el Observer             — ADMINISTRADOR
 *   GET    /api/inscripciones/{cedula}   200 | 404                                         — ADMINISTRADOR o el propio cliente
 *   POST   /api/inscripciones            201 + Location | 400 | 404 (curso) | 409          — público (registro)
 *   PUT    /api/inscripciones/{cedula}   200 | 400 (intenta cambiar PK/FK) | 404           — ADMINISTRADOR o el propio cliente
 *   DELETE /api/inscripciones/{cedula}   204 (desinscribirse) | 404 | 409                  — ADMINISTRADOR o el propio cliente
 *
 * "El propio cliente": la cédula de la URL debe ser la de la sesión (authentication.name);
 * con cualquier otra cédula la respuesta es 403.
 */
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final AutenticacionService autenticacionService;
    private final EstadisticasInscripciones estadisticasInscripciones;

    public InscripcionController(InscripcionService inscripcionService, AutenticacionService autenticacionService,
                                 EstadisticasInscripciones estadisticasInscripciones) {
        this.inscripcionService = inscripcionService;
        this.autenticacionService = autenticacionService;
        this.estadisticasInscripciones = estadisticasInscripciones;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<InscripcionRespuestaDTO> listar() {
        return inscripcionService.listar();
    }

    /** Ejemplo de uso de la HttpSession: el cliente solo ve su propia inscripción. */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mia")
    public InscripcionRespuestaDTO mia(HttpSession sesion) {
        String cedula = autenticacionService.usuarioActual(sesion).cedula();
        return inscripcionService.obtener(cedula);
    }

    /** Patrón Observer: datos que lleva el observador EstadisticasInscripciones. */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/estadisticas")
    public Map<String, Object> estadisticas() {
        return estadisticasInscripciones.resumen();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('CLIENTE') and #cedula == authentication.name)")
    @GetMapping("/{cedula}")
    public InscripcionRespuestaDTO obtener(@PathVariable String cedula) {
        return inscripcionService.obtener(cedula);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Validated({Default.class, AlCrear.class}) @RequestBody InscripcionDTO datos) {
        InscripcionRespuestaDTO creada = inscripcionService.crear(datos);
        String detalle = "curso".equals(creada.tipoInscripcion()) ? "un curso grupal" : "una rutina personalizada";

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", "¡Bienvenido/a, " + creada.nombre() + "! Tu inscripción a " + detalle + " quedó registrada.");
        cuerpo.put("inscripcion", creada);
        return ResponseEntity.created(URI.create("/api/inscripciones/" + creada.cedula())).body(cuerpo);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('CLIENTE') and #cedula == authentication.name)")
    @PutMapping("/{cedula}")
    public ResultadoOperacionDTO<InscripcionRespuestaDTO> actualizar(@PathVariable String cedula,
                                                                     @Valid @RequestBody InscripcionDTO cambios) {
        InscripcionRespuestaDTO actualizada = inscripcionService.actualizar(cedula, cambios);
        return new ResultadoOperacionDTO<>("Inscripción actualizada correctamente.", 1, actualizada);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('CLIENTE') and #cedula == authentication.name)")
    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminar(@PathVariable String cedula) {
        inscripcionService.eliminar(cedula);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
