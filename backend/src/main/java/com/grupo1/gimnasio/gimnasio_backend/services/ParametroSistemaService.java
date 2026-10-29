package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.ParametroSistemaDTO;

import java.util.List;

/** Parámetros del sistema (clave/valor). Implementación: ParametroSistemaServiceImpl. */
public interface ParametroSistemaService {

    List<ParametroSistemaDTO> listar();

    /** Solo cambia el valor de una clave existente (404 si no existe). */
    ParametroSistemaDTO actualizar(String clave, ParametroSistemaDTO cambios);
}
