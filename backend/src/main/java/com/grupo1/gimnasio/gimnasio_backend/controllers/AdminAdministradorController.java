package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.AdministradorDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.AdministradorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Gestión de administradores — /api/admin/administradores (solo ADMINISTRADOR)
 *
 *   GET    /api/admin/administradores         200 lista (sin contraseñas)
 *   POST   /api/admin/administradores         201 + Location | 400
 *   DELETE /api/admin/administradores/{cod}   204 | 404 | 409 (la propia cuenta o el último administrador)
 */
@RestController
@RequestMapping("/api/admin/administradores")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminAdministradorController {

    private final AdministradorService administradorService;

    public AdminAdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public List<AdministradorDTO> listar() {
        return administradorService.listar();
    }

    @PostMapping
    public ResponseEntity<AdministradorDTO> crear(@Valid @RequestBody AdministradorDTO datos) {
        AdministradorDTO creado = administradorService.crear(datos);
        return ResponseEntity.created(URI.create("/api/admin/administradores/" + creado.codAdministrador())).body(creado);
    }

    @DeleteMapping("/{codAdministrador}")
    public ResponseEntity<Void> eliminar(@PathVariable long codAdministrador, Authentication autenticacion) {
        administradorService.eliminar(codAdministrador, Long.parseLong(autenticacion.getName()));
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
