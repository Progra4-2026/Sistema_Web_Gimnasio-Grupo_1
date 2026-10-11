package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla maquinas. */
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
}
