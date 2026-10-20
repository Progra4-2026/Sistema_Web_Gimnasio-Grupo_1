package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionRespuestaDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;

import java.util.List;

/**
 * Lógica de negocio de las inscripciones de clientes. Implementación: InscripcionServiceImpl.
 */
public interface InscripcionService {

    List<InscripcionRespuestaDTO> listar();

    InscripcionRespuestaDTO obtener(String cedula);

    /** 409 si la cédula ya está inscrita o el curso no tiene cupos. */
    InscripcionRespuestaDTO crear(InscripcionDTO inscripcion);

    /** Actualiza datos de contacto. No permite cambiar la cédula (PK) ni el curso (FK). */
    InscripcionRespuestaDTO actualizar(String cedula, InscripcionDTO cambios);

    /** Desinscribe al cliente y libera su cupo. */
    void eliminar(String cedula);

    /** Verifica cédula y contraseña. 401 si no coinciden. */
    UsuarioSesionDTO autenticar(String cedula, String password);
}
