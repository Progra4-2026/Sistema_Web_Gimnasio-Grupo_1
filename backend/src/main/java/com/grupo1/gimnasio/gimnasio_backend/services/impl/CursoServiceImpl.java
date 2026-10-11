package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.CursoDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.services.CursoService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Reglas de negocio de los cursos:
 *  - descripción obligatoria (3-50, igual que VARCHAR(50) en la tabla cursos) y sin duplicados;
 *  - horario obligatorio y cupos entre 0 y 100;
 *  - el id (llave primaria) lo asigna el servidor y no se puede modificar;
 *  - no se puede borrar un curso con clientes inscritos;
 *  - cálculo de cupos: inscribir resta un cupo y desinscribir lo devuelve.
 *
 * Patrón Singleton: bean de alcance singleton (una sola instancia en todo el contenedor de Spring).
 *
 * Los datos se mantienen en memoria hasta que exista CursoRepository (JpaRepository);
 * entonces solo cambian las líneas que leen o escriben en "cursos" e "inscritos".
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CursoServiceImpl implements CursoService {

    private static final Pattern IMAGEN = Pattern.compile("^[\\w-]+\\.(jpg|jpeg|png|webp)$");
    private static final String IMAGEN_POR_DEFECTO = "clases_grupales.jpg";

    private final Map<Integer, CursoDTO> cursos = new ConcurrentHashMap<>();
    private final Map<Integer, AtomicInteger> inscritos = new ConcurrentHashMap<>();
    private final AtomicInteger secuencia = new AtomicInteger();

    public CursoServiceImpl() {
        guardarNuevo(new CursoDTO(null, "Spinning", "Clase cardiovascular de alta intensidad en bicicleta estática.",
                "Lun/Mié/Vie, 6:00 a.m.", 8, "clase_spinning.jpg"));
        guardarNuevo(new CursoDTO(null, "Yoga funcional", "Ejercicios de flexibilidad, respiración y relajación.",
                "Mar/Jue, 7:00 p.m.", 12, "clase_yoga.jpg"));
        guardarNuevo(new CursoDTO(null, "CrossFit", "Entrenamiento funcional de alta intensidad en grupo.",
                "Lun a Vie, 5:30 p.m.", 0, "clase_crossfit.jpg"));
        guardarNuevo(new CursoDTO(null, "Clases grupales", "Aeróbicos y circuito funcional para todos los niveles.",
                "Sáb, 9:00 a.m.", 15, "clases_grupales.jpg"));
    }

    @Override
    public List<CursoDTO> listar() {
        List<CursoDTO> lista = new ArrayList<>(cursos.values());
        lista.sort((a, b) -> Integer.compare(a.id(), b.id()));
        return lista;
    }

    @Override
    public CursoDTO obtener(int id) {
        CursoDTO curso = cursos.get(id);
        if (curso == null) throw new RecursoNoEncontradoException("un curso", id);
        return curso;
    }

    @Override
    public synchronized CursoDTO crear(CursoDTO curso) {
        CursoDTO limpio = validar(curso);
        verificarDescripcionUnica(limpio.descripcion(), null);
        return guardarNuevo(limpio);
    }

    @Override
    public synchronized CursoDTO actualizar(int id, CursoDTO cambios) {
        obtener(id); // 404 si no existe
        if (cambios.id() != null && cambios.id() != id) {
            throw new DatosInvalidosException("No se permite modificar el identificador del curso.");
        }
        CursoDTO limpio = validar(cambios);
        verificarDescripcionUnica(limpio.descripcion(), id);

        CursoDTO actualizado = new CursoDTO(id, limpio.descripcion(), limpio.detalle(), limpio.horario(),
                limpio.cupos(), limpio.imagen());
        cursos.put(id, actualizado);
        return actualizado;
    }

    @Override
    public synchronized void eliminar(int id) {
        CursoDTO curso = obtener(id);
        int cantidad = inscritos.get(id).get();
        if (cantidad > 0) {
            throw new ConflictoException("No se puede eliminar el curso \"" + curso.descripcion()
                    + "\" porque tiene " + cantidad + " cliente(s) inscrito(s). Desinscribalos primero.");
        }
        cursos.remove(id);
        inscritos.remove(id);
    }

    @Override
    public synchronized void reservarCupo(int id) {
        CursoDTO curso = obtener(id);
        if (curso.cupos() <= 0) {
            throw new ConflictoException("El curso \"" + curso.descripcion() + "\" no tiene cupos disponibles.");
        }
        cursos.put(id, conCupos(curso, curso.cupos() - 1));
        inscritos.get(id).incrementAndGet();
    }

    @Override
    public synchronized void liberarCupo(int id) {
        CursoDTO curso = cursos.get(id);
        if (curso == null) return;
        cursos.put(id, conCupos(curso, curso.cupos() + 1));
        inscritos.get(id).updateAndGet(n -> Math.max(0, n - 1));
    }

    /* ---------- auxiliares ---------- */

    private CursoDTO guardarNuevo(CursoDTO curso) {
        int id = secuencia.incrementAndGet();
        CursoDTO conId = new CursoDTO(id, curso.descripcion(), curso.detalle(), curso.horario(),
                curso.cupos(), curso.imagen());
        cursos.put(id, conId);
        inscritos.put(id, new AtomicInteger());
        return conId;
    }

    private static CursoDTO conCupos(CursoDTO c, int cupos) {
        return new CursoDTO(c.id(), c.descripcion(), c.detalle(), c.horario(), cupos, c.imagen());
    }

    private void verificarDescripcionUnica(String descripcion, Integer idPropio) {
        for (CursoDTO existente : cursos.values()) {
            if (existente.descripcion().equalsIgnoreCase(descripcion) && !existente.id().equals(idPropio)) {
                throw new ConflictoException("Ya existe un curso llamado \"" + existente.descripcion() + "\".");
            }
        }
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
