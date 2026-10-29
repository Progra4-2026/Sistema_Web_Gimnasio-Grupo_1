package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** CRUD de la tabla rutinas. */
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    boolean existsByCliente_Cedula(Long cedula);

    boolean existsByInstructor_CodInstructor(Long codInstructor);

    boolean existsByMaquina_IdMaquina(Long idMaquina);

    /** Rutinas de un cliente (consulta derivada: Hibernate la ejecuta con un parámetro "?"). */
    List<Rutina> findByCliente_CedulaOrderByFechaDesc(Long cedula);
}
