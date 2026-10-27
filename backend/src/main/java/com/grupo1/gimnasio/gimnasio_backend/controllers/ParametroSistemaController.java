package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.ParametroSistemaDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.ResultadoOperacionDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.ParametroSistemaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Parámetros del sistema que gestiona el administrador — /api/admin/parametros. */
@RestController
@RequestMapping("/api/admin/parametros")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ParametroSistemaController extends ControladorBase {

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
                                                                  @RequestBody ParametroSistemaDTO cambios) {
        ParametroSistemaDTO actualizado = parametroSistemaService.actualizar(clave, cambios);
        return new ResultadoOperacionDTO<>("Parámetro actualizado correctamente.", 1, actualizado);
    }
}
