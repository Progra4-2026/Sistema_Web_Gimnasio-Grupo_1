package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.ParametroSistemaDTO;

import java.util.List;

/** Parámetros del sistema que gestiona el administrador. Implementación: ParametroSistemaServiceImpl. */
public interface ParametroSistemaService {

    List<ParametroSistemaDTO> listar();

    ParametroSistemaDTO actualizar(String clave, ParametroSistemaDTO cambios);
}
