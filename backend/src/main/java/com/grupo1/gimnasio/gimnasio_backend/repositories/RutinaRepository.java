package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla rutinas. */
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    boolean existsByCliente_Cedula(Long cedula);
}
