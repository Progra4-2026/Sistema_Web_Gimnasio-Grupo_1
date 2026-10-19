package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** CRUD de la tabla consultas. */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findAllByOrderByFechaDesc();
}
