package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.HistorialCurso;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla historial_curso. */
public interface HistorialCursoRepository extends JpaRepository<HistorialCurso, Long> {

    boolean existsByCliente_Cedula(Long cedula);

    boolean existsByCurso_IdCurso(Long idCurso);
}
