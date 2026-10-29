package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.InstructorDTO;

import java.util.List;

/** Gestión de instructores, a cargo del administrador. Implementación: InstructorServiceImpl. */
public interface InstructorService {

    List<InstructorDTO> listar();

    InstructorDTO obtener(long codInstructor);

    /** La contraseña es obligatoria al crear y se guarda cifrada con BCrypt. */
    InstructorDTO crear(InstructorDTO datos);

    /** No permite cambiar el código (llave primaria); la contraseña solo cambia si se envía. */
    InstructorDTO actualizar(long codInstructor, InstructorDTO cambios);

    /** No permite borrar un instructor con rutinas o historial de cursos (409). */
    void eliminar(long codInstructor);
}
