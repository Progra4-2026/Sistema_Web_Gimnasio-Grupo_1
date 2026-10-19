package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla instructores. */
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
}
