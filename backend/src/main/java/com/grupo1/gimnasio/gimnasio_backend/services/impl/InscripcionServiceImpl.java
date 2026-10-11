package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionRespuestaDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.NoAutenticadoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.services.CursoService;
import com.grupo1.gimnasio.gimnasio_backend.services.InscripcionService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Reglas de negocio de las inscripciones:
 *  - validación de nombre, cédula, correo, teléfono, fecha de nacimiento y contraseña;
 *  - cálculo de la edad a partir de la fecha de nacimiento (mínimo 14 años, igual que el frontend);
 *  - una sola inscripción por cédula (409 si se repite);
 *  - curso: debe existir y tener cupos (se reserva uno); rutina: objetivo válido;
 *  - al actualizar no se cambia la cédula (PK) ni el curso (FK);
 *  - al desinscribir se libera el cupo del curso;
 *  - la contraseña se guarda cifrada con hash + salt, nunca en texto plano.
 *
 * Patrón Singleton: bean de alcance singleton.
 * Datos en memoria hasta que exista ClienteRepository (JpaRepository).
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InscripcionServiceImpl implements InscripcionService {

    static final int EDAD_MINIMA = 14;
    private static final Set<String> TIPOS = Set.of("rutina", "curso");
    private static final Set<String> OBJETIVOS = Set.of("fuerza", "resistencia", "peso", "flexibilidad");

    private final CursoService cursoService;
    private final Map<String, Inscripcion> inscripciones = new ConcurrentHashMap<>();

    /** Inyección por constructor: Spring entrega el único bean de CursoService. */
    public InscripcionServiceImpl(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @Override
    public List<InscripcionRespuestaDTO> listar() {
        List<InscripcionRespuestaDTO> lista = new ArrayList<>();
        for (Inscripcion ins : inscripciones.values()) lista.add(aRespuesta(ins));
        lista.sort((a, b) -> a.nombre().compareToIgnoreCase(b.nombre()));
        return lista;
    }

    @Override
    public InscripcionRespuestaDTO obtener(String cedula) {
        return aRespuesta(buscar(cedula));
    }

    @Override
    public synchronized InscripcionRespuestaDTO crear(InscripcionDTO datos) {
        Map<String, String> errores = new LinkedHashMap<>();
        validarDatosPersonales(datos, errores);
        requerido(errores, "cedula", datos.cedula());
        patron(errores, "cedula", datos.cedula(), CEDULA, "La cédula debe tener entre 9 y 12 dígitos.");
        requerido(errores, "password", datos.password());
        if (!vacio(datos.password()) && datos.password().length() < 8) {
            errores.put("password", "La contraseña debe tener al menos 8 caracteres.");
        }
        Integer idCurso = validarTipo(datos, errores);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        String cedula = datos.cedula().trim();
        if (inscripciones.containsKey(cedula)) {
            throw new ConflictoException("Ya existe una inscripción con la cédula " + cedula + ".");
        }
        if (idCurso != null) cursoService.reservarCupo(idCurso); // 404 o 409 si no se puede

        Inscripcion nueva = new Inscripcion();
        nueva.cedula = cedula;
        nueva.tipo = datos.tipoInscripcion();
        nueva.idCurso = idCurso;
        nueva.objetivo = "rutina".equals(nueva.tipo) ? datos.objetivo() : null;
        nueva.hashContrasena = HashContrasenas.cifrar(datos.password());
        nueva.fechaInscripcion = LocalDate.now();
        copiarDatosPersonales(datos, nueva);
        inscripciones.put(cedula, nueva);
        return aRespuesta(nueva);
    }

    @Override
    public synchronized InscripcionRespuestaDTO actualizar(String cedula, InscripcionDTO cambios) {
        Inscripcion actual = buscar(cedula);

        if (cambios.cedula() != null && !cambios.cedula().trim().equals(actual.cedula)) {
            throw new DatosInvalidosException("No se permite modificar la cédula (llave primaria).");
        }
        if (cambios.idCurso() != null && !Objects.equals(aEntero(cambios.idCurso()), actual.idCurso)) {
            throw new DatosInvalidosException(
                    "No se permite cambiar el curso (llave foránea). Desinscribite y volvé a inscribirte.");
        }
        if (cambios.tipoInscripcion() != null && !cambios.tipoInscripcion().equals(actual.tipo)) {
            throw new DatosInvalidosException("No se permite cambiar el tipo de inscripción.");
        }

        Map<String, String> errores = new LinkedHashMap<>();
        validarDatosPersonales(cambios, errores);
        if ("rutina".equals(actual.tipo) && cambios.objetivo() != null && !OBJETIVOS.contains(cambios.objetivo())) {
            errores.put("objetivo", "Seleccioná un objetivo válido.");
        }
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        copiarDatosPersonales(cambios, actual);
        if ("rutina".equals(actual.tipo) && cambios.objetivo() != null) actual.objetivo = cambios.objetivo();
        return aRespuesta(actual);
    }

    @Override
    public synchronized void eliminar(String cedula) {
        Inscripcion ins = buscar(cedula);
        inscripciones.remove(ins.cedula);
        if (ins.idCurso != null) cursoService.liberarCupo(ins.idCurso);
    }

    @Override
    public UsuarioSesionDTO autenticar(String cedula, String password) {
        Inscripcion ins = vacio(cedula) ? null : inscripciones.get(cedula.trim());
        // Mismo mensaje si la cédula no existe o la contraseña es incorrecta: no revela cuál falló
        if (ins == null || vacio(password) || !HashContrasenas.coincide(password, ins.hashContrasena)) {
            throw new NoAutenticadoException("Cédula o contraseña incorrectas.");
        }
        return new UsuarioSesionDTO(ins.cedula, ins.nombre, "cliente");
    }

    /* ---------- auxiliares ---------- */

    private Inscripcion buscar(String cedula) {
        Inscripcion ins = cedula == null ? null : inscripciones.get(cedula.trim());
        if (ins == null) throw new RecursoNoEncontradoException("una inscripción", cedula);
        return ins;
    }

    private static void validarDatosPersonales(InscripcionDTO d, Map<String, String> errores) {
        requerido(errores, "nombre", d.nombre());
        longitud(errores, "nombre", d.nombre(), 3, 80);
        requerido(errores, "email", d.email());
        patron(errores, "email", d.email(), EMAIL, "Correo electrónico inválido.");
        requerido(errores, "telefono", d.telefono());
        patron(errores, "telefono", d.telefono(), TELEFONO, "El teléfono debe tener 8 dígitos.");
        requerido(errores, "fechaNacimiento", d.fechaNacimiento());
        if (!vacio(d.fechaNacimiento())) {
            try {
                LocalDate nacimiento = LocalDate.parse(d.fechaNacimiento().trim());
                if (nacimiento.isAfter(LocalDate.now())) {
                    errores.put("fechaNacimiento", "La fecha de nacimiento no puede ser futura.");
                } else if (calcularEdad(nacimiento) < EDAD_MINIMA) {
                    errores.put("fechaNacimiento", "Debés tener al menos " + EDAD_MINIMA + " años.");
                }
            } catch (DateTimeParseException e) {
                errores.put("fechaNacimiento", "Usá el formato AAAA-MM-DD.");
            }
        }
    }

    /** Valida tipo + curso/objetivo y devuelve el id del curso (o null si es rutina). */
    private static Integer validarTipo(InscripcionDTO d, Map<String, String> errores) {
        if (vacio(d.tipoInscripcion()) || !TIPOS.contains(d.tipoInscripcion())) {
            errores.put("tipoInscripcion", "Seleccioná rutina o curso.");
            return null;
        }
        if ("curso".equals(d.tipoInscripcion())) {
            Integer id = aEntero(d.idCurso());
            if (id == null) errores.put("idCurso", "Seleccioná un curso válido.");
            return id;
        }
        if (vacio(d.objetivo()) || !OBJETIVOS.contains(d.objetivo())) {
            errores.put("objetivo", "Seleccioná un objetivo válido.");
        }
        return null;
    }

    private static void copiarDatosPersonales(InscripcionDTO d, Inscripcion destino) {
        destino.nombre = limpiar(d.nombre());
        destino.email = limpiar(d.email());
        destino.telefono = limpiar(d.telefono());
        destino.fechaNacimiento = LocalDate.parse(d.fechaNacimiento().trim());
    }

    static int calcularEdad(LocalDate nacimiento) {
        return Period.between(nacimiento, LocalDate.now()).getYears();
    }

    private static Integer aEntero(String valor) {
        if (vacio(valor)) return null;
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static InscripcionRespuestaDTO aRespuesta(Inscripcion i) {
        return new InscripcionRespuestaDTO(i.cedula, i.nombre, i.email, i.telefono,
                i.fechaNacimiento.toString(), calcularEdad(i.fechaNacimiento), i.tipo,
                i.idCurso, i.objetivo, i.fechaInscripcion.toString());
    }

    /** Registro interno. Se reemplaza por la entidad Cliente cuando exista. */
    private static final class Inscripcion {
        String cedula;
        String nombre;
        String email;
        String telefono;
        LocalDate fechaNacimiento;
        String tipo;
        Integer idCurso;
        String objetivo;
        String hashContrasena;
        LocalDate fechaInscripcion;
    }
}
