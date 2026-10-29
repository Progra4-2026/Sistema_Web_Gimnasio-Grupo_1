package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla administradores. */
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
}
