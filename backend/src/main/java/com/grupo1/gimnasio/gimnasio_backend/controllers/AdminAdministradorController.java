package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.AdministradorDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.AdministradorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Gestión de cuentas de administrador — /api/admin/administradores. */
@RestController
@RequestMapping("/api/admin/administradores")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminAdministradorController extends ControladorBase {

    private final AdministradorService administradorService;

    public AdminAdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public List<AdministradorDTO> listar() {
        return administradorService.listar();
    }

    @PostMapping
    public ResponseEntity<AdministradorDTO> crear(@RequestBody AdministradorDTO administrador) {
        AdministradorDTO creado = administradorService.crear(administrador);
        return ResponseEntity.created(URI.create("/api/admin/administradores/" + creado.codAdministrador()))
                .body(creado);
    }

    @DeleteMapping("/{codAdministrador}")
    public ResponseEntity<Void> eliminar(@PathVariable int codAdministrador) {
        administradorService.eliminar(codAdministrador);
        return ResponseEntity.noContent().header(Encabezados.REGISTROS_ELIMINADOS, "1").build();
    }
}
