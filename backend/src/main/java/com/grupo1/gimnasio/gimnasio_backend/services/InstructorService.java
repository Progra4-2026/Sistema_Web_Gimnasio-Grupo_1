package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.InstructorDTO;

import java.util.List;

/** Administración de instructores (alta/edición/borrado). Implementación: InstructorServiceImpl. */
public interface InstructorService {

    List<InstructorDTO> listar();

    InstructorDTO obtener(int codInstructor);

    InstructorDTO crear(InstructorDTO instructor);

    InstructorDTO actualizar(int codInstructor, InstructorDTO cambios);

    void eliminar(int codInstructor);
}
