package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.ConsultaDTO;

import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio de las consultas del formulario de Contactos. Implementación: ConsultaServiceImpl.
 */
public interface ConsultaService {

    List<Map<String, Object>> listar();

    /** Registra la consulta y devuelve su id y un mensaje de confirmación. */
    Map<String, Object> crear(ConsultaDTO consulta);
}
