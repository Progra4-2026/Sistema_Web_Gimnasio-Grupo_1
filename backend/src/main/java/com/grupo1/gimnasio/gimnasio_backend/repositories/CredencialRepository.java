package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla credenciales (llave: la cédula del cliente). */
public interface CredencialRepository extends JpaRepository<Credencial, Long> {
}
