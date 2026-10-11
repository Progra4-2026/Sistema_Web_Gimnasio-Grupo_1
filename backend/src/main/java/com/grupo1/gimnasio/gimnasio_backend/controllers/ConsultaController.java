package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.ConsultaDTO;
import com.grupo1.gimnasio.gimnasio_backend.services.ConsultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Consultas del formulario de Contactos — /api/consultas
 *
 *   GET  /api/consultas   200 lista
 *   POST /api/consultas   201 | 400 con errores por campo
 */
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController extends ControladorBase {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return consultaService.listar();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody ConsultaDTO consulta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.crear(consulta));
    }
}
