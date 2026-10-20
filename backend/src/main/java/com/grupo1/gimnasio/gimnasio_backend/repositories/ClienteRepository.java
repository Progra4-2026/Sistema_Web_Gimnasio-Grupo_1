package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla cliente (llave: cédula). */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Cantidad de clientes inscritos en un curso (para no borrar cursos con inscritos). */
    long countByCursoInscrito_IdCurso(Long idCurso);
}
