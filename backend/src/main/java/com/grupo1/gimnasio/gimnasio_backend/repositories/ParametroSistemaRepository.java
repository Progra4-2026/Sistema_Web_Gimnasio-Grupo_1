package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.ParametroSistema;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla parametros_sistema (llave: la clave del parámetro). */
public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, String> {
}
