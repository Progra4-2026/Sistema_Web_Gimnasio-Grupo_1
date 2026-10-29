package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.ParametroSistemaDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.ParametroSistemaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Parámetros del sistema — /api/admin/parametros (solo ADMINISTRADOR)
 *
 *   GET /api/admin/parametros           200 lista
 *   PUT /api/admin/parametros/{clave}   200 | 400 | 404 (solo cambia el valor)
 */
@RestController
@RequestMapping("/api/admin/parametros")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ParametroSistemaController {

    private final ParametroSistemaService parametroSistemaService;

    public ParametroSistemaController(ParametroSistemaService parametroSistemaService) {
        this.parametroSistemaService = parametroSistemaService;
    }

    @GetMapping
    public List<ParametroSistemaDTO> listar() {
        return parametroSistemaService.listar();
    }

    @PutMapping("/{clave}")
    public ResultadoOperacionDTO<ParametroSistemaDTO> actualizar(@PathVariable String clave,
                                                                 @Valid @RequestBody ParametroSistemaDTO cambios) {
        return new ResultadoOperacionDTO<>("Parámetro actualizado correctamente.", 1,
                parametroSistemaService.actualizar(clave, cambios));
    }
}
