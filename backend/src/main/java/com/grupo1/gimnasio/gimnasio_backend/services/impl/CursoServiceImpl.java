package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.CursoDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.models.Curso;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.CursoRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.HistorialCursoRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.CursoService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Reglas de negocio de los cursos:
 *  - descripción obligatoria (3-50, igual que VARCHAR(50) en la tabla cursos) y sin duplicados;
 *  - horario obligatorio y cupos entre 0 y 100;
 *  - el id (llave primaria) lo asigna la base de datos y no se puede modificar;
 *  - no se puede borrar un curso con clientes inscritos ni con historial;
 *  - cálculo de cupos: inscribir resta un cupo y desinscribir lo devuelve.
 *
 * Patrón Singleton: bean de alcance singleton (una sola instancia en todo el contenedor de Spring).
 * Patrón Iterator: listar() recorre los cursos con java.util.Iterator.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CursoServiceImpl implements CursoService {

    private static final Pattern IMAGEN = Pattern.compile("^[\\w-]+\\.(jpg|jpeg|png|webp)$");
    private static final String IMAGEN_POR_DEFECTO = "clases_grupales.jpg";

    private final CursoRepository cursoRepository;
    private final ClienteRepository clienteRepository;
    private final HistorialCursoRepository historialCursoRepository;

    public CursoServiceImpl(CursoRepository cursoRepository, ClienteRepository clienteRepository,
                            HistorialCursoRepository historialCursoRepository) {
        this.cursoRepository = cursoRepository;
        this.clienteRepository = clienteRepository;
        this.historialCursoRepository = historialCursoRepository;
    }

    /**
     * PATRÓN ITERATOR: se recorre la colección de entidades Curso con java.util.Iterator
     * (hasNext / next) para convertir cada una al DTO que devuelve la API.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CursoDTO> listar() {
        List<Curso> cursos = cursoRepository.findAll(Sort.by("idCurso"));
        List<CursoDTO> lista = new ArrayList<>(cursos.size());

        Iterator<Curso> iterador = cursos.iterator();
        while (iterador.hasNext()) {
            lista.add(aDTO(iterador.next()));
        }
        return lista;
    }

    @Override
    @Transactional(readOnly = true)
    public CursoDTO obtener(int id) {
        return aDTO(buscar(id));
    }

    @Override
    @Transactional
    public CursoDTO crear(CursoDTO datos) {
        CursoDTO limpio = validar(datos);
        if (cursoRepository.existsByDescripcionIgnoreCase(limpio.descripcion())) {
            throw new ConflictoException("Ya existe un curso llamado \"" + limpio.descripcion() + "\".");
        }
        Curso curso = new Curso();
        copiar(limpio, curso);
        return aDTO(cursoRepository.save(curso));
    }

    @Override
    @Transactional
    public CursoDTO actualizar(int id, CursoDTO cambios) {
        Curso curso = buscar(id); // 404 si no existe
        if (cambios.id() != null && cambios.id() != id) {
            throw new DatosInvalidosException("No se permite modificar el identificador del curso.");
        }
        CursoDTO limpio = validar(cambios);
        if (cursoRepository.existsByDescripcionIgnoreCaseAndIdCursoNot(limpio.descripcion(), (long) id)) {
            throw new ConflictoException("Ya existe un curso llamado \"" + limpio.descripcion() + "\".");
        }
        copiar(limpio, curso);
        return aDTO(cursoRepository.save(curso));
    }

    @Override
    @Transactional
    public void eliminar(int id) {
        Curso curso = buscar(id);
        long inscritos = clienteRepository.countByCursoInscrito_IdCurso((long) id);
        if (inscritos > 0) {
            throw new ConflictoException("No se puede eliminar el curso \"" + curso.getDescripcion()
                    + "\" porque tiene " + inscritos + " cliente(s) inscrito(s). Desinscribalos primero.");
        }
        if (historialCursoRepository.existsByCurso_IdCurso((long) id)) {
            throw new ConflictoException("No se puede eliminar el curso \"" + curso.getDescripcion()
                    + "\" porque tiene registros en el historial de cursos.");
        }
        cursoRepository.delete(curso);
    }

    @Override
    @Transactional
    public void reservarCupo(int id) {
        Curso curso = buscar(id);
        if (curso.getCupos() <= 0) {
            throw new ConflictoException("El curso \"" + curso.getDescripcion() + "\" no tiene cupos disponibles.");
        }
        curso.setCupos(curso.getCupos() - 1);
        cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void liberarCupo(int id) {
        cursoRepository.findById((long) id).ifPresent(curso -> {
            curso.setCupos(curso.getCupos() + 1);
            cursoRepository.save(curso);
        });
    }

    /* ---------- auxiliares ---------- */

    private Curso buscar(int id) {
        return cursoRepository.findById((long) id)
                .orElseThrow(() -> new RecursoNoEncontradoException("un curso", id));
    }

    private static CursoDTO aDTO(Curso c) {
        return new CursoDTO(c.getIdCurso().intValue(), c.getDescripcion(), c.getDetalle(), c.getHorario(),
                c.getCupos(), c.getImagen());
    }

    private static void copiar(CursoDTO origen, Curso destino) {
        destino.setDescripcion(origen.descripcion());
        destino.setDetalle(origen.detalle());
        destino.setHorario(origen.horario());
        destino.setCupos(origen.cupos());
        destino.setImagen(origen.imagen());
    }

    /** Valida y devuelve una copia con los textos sin espacios sobrantes. */
    private static CursoDTO validar(CursoDTO curso) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "descripcion", curso.descripcion());
        longitud(errores, "descripcion", curso.descripcion(), 3, 50);
        longitud(errores, "detalle", curso.detalle(), 0, 300);
        requerido(errores, "horario", curso.horario());
        longitud(errores, "horario", curso.horario(), 3, 60);
        if (curso.cupos() == null) {
            errores.put("cupos", "Este campo es obligatorio.");
        } else if (curso.cupos() < 0 || curso.cupos() > 100) {
            errores.put("cupos", "Los cupos deben estar entre 0 y 100.");
        }
        patron(errores, "imagen", curso.imagen(), IMAGEN, "La imagen debe ser un archivo .jpg, .jpeg, .png o .webp.");

        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        String imagen = vacio(curso.imagen()) ? IMAGEN_POR_DEFECTO : curso.imagen().trim();
        return new CursoDTO(curso.id(), limpiar(curso.descripcion()), limpiar(curso.detalle()),
                limpiar(curso.horario()), curso.cupos(), imagen);
    }
}
