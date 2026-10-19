package com.grupo1.gimnasio.gimnasio_backend.repositories;

import com.grupo1.gimnasio.gimnasio_backend.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD de la tabla cursos. */
public interface CursoRepository extends JpaRepository<Curso, Long> {

    boolean existsByDescripcionIgnoreCase(String descripcion);

    /** Para verificar que otro curso (distinto al que se actualiza) no tenga la misma descripción. */
    boolean existsByDescripcionIgnoreCaseAndIdCursoNot(String descripcion, Long idCurso);
}
