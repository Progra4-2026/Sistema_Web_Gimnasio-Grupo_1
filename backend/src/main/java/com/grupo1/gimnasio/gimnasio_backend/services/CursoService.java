package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.CursoDTO;

import java.util.List;

/**
 * Lógica de negocio de los cursos. Implementación: CursoServiceImpl.
 * Los controladores dependen de esta interfaz, no de la implementación.
 */
public interface CursoService {

    List<CursoDTO> listar();

    /** @throws com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException si no existe */
    CursoDTO obtener(int id);

    CursoDTO crear(CursoDTO curso);

    /** No permite cambiar el id (llave primaria). */
    CursoDTO actualizar(int id, CursoDTO curso);

    /** No permite borrar un curso con clientes inscritos (409). */
    void eliminar(int id);

    /** Ocupa un cupo del curso al inscribir a un cliente (409 si no hay cupos). */
    void reservarCupo(int id);

    /** Libera el cupo cuando un cliente se desinscribe. */
    void liberarCupo(int id);
}
