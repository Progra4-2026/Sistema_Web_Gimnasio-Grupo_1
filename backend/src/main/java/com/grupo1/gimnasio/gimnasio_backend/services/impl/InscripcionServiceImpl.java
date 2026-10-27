package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionRespuestaDTO;
import com.grupo1.gimnasio.gimnasio_backend.entities.Cliente;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.CursoService;
import com.grupo1.gimnasio.gimnasio_backend.services.InscripcionService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 *  - validación de nombre, apellidos, cédula, correo, teléfono, fecha de nacimiento y contraseña;
 *  - cálculo de la edad a partir de la fecha de nacimiento (mínimo 14 años, igual que el frontend);
 *  - una sola inscripción por cédula (409 si se repite);
 *  - curso: debe existir y tener cupos (se reserva uno); rutina: objetivo válido;
 *  - al actualizar no se cambia la cédula (PK) ni el curso (FK);
 *  - al desinscribir se libera el cupo del curso;
 *  - la contraseña se guarda cifrada con BCrypt (hash + salt), nunca en texto plano.
 *
 * tipoInscripcion/idCurso/objetivo no tienen columna propia en la tabla "cliente" (nunca
 * la tuvieron): se devuelven en la respuesta de crear(), pero listar()/obtener() no pueden
 * "recordarlos" después porque no hay dónde guardarlos sin inventar una segunda fuente de
 * verdad — es una consecuencia documentada, no un bug. El único dato de ese trío que sí se
 * recuerda internamente es el curso elegido (para poder liberar el cupo al desinscribirse).
 *
 * Patrón Singleton: bean de alcance singleton.
 * Persistencia real en MySQL vía ClienteRepository (JpaRepository).
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InscripcionServiceImpl implements InscripcionService {

    static final int EDAD_MINIMA = 14;
    private static final Set<String> TIPOS = Set.of("rutina", "curso");
    private static final Set<String> OBJETIVOS = Set.of("fuerza", "resistencia", "peso", "flexibilidad");

    private final CursoService cursoService;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    /** Cédula -> curso elegido al inscribirse, solo para poder liberar el cupo al desinscribirse. */
    private final Map<Integer, Integer> cursoPorCliente = new ConcurrentHashMap<>();

    public InscripcionServiceImpl(CursoService cursoService, ClienteRepository clienteRepository,
                                   PasswordEncoder passwordEncoder) {
        this.cursoService = cursoService;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<InscripcionRespuestaDTO> listar() {
        List<InscripcionRespuestaDTO> lista = new ArrayList<>();
        for (Cliente c : clienteRepository.findAll()) lista.add(aRespuesta(c, null));
        lista.sort((a, b) -> a.nombre().compareToIgnoreCase(b.nombre()));
        return lista;
    }

    @Override
    public InscripcionRespuestaDTO obtener(String cedula) {
        Cliente c = buscar(cedula);
        return aRespuesta(c, null);
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

        Integer cedula = Integer.valueOf(datos.cedula().trim());
        if (clienteRepository.existsById(cedula)) {
            throw new ConflictoException("Ya existe una inscripción con la cédula " + cedula + ".");
        }
        if (idCurso != null) cursoService.reservarCupo(idCurso); // 404 o 409 si no se puede

        Cliente nuevo = new Cliente();
        nuevo.setCedula(cedula);
        nuevo.setTelHabitacion(0);
        nuevo.setFechaInscripcion(LocalDate.now());
        nuevo.setContrasena(passwordEncoder.encode(datos.password()));
        copiarDatosPersonales(datos, nuevo);
        clienteRepository.save(nuevo);

        if (idCurso != null) cursoPorCliente.put(cedula, idCurso);
        return aRespuesta(nuevo, datos);
    }

    @Override
    public synchronized InscripcionRespuestaDTO actualizar(String cedula, InscripcionDTO cambios) {
        Cliente actual = buscar(cedula);

        if (cambios.cedula() != null && !cambios.cedula().trim().equals(String.valueOf(actual.getCedula()))) {
            throw new DatosInvalidosException("No se permite modificar la cédula (llave primaria).");
        }
        Integer idCursoActual = cursoPorCliente.get(actual.getCedula());
        if (cambios.idCurso() != null && !Objects.equals(aEntero(cambios.idCurso()), idCursoActual)) {
            throw new DatosInvalidosException(
                    "No se permite cambiar el curso (llave foránea). Desinscribite y volvé a inscribirte.");
        }

        Map<String, String> errores = new LinkedHashMap<>();
        validarDatosPersonales(cambios, errores);
        if (cambios.objetivo() != null && !OBJETIVOS.contains(cambios.objetivo())) {
            errores.put("objetivo", "Seleccioná un objetivo válido.");
        }
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        copiarDatosPersonales(cambios, actual);
        clienteRepository.save(actual);
        return aRespuesta(actual, cambios);
    }

    @Override
    public synchronized void eliminar(String cedula) {
        Cliente c = buscar(cedula);
        clienteRepository.delete(c);
        Integer idCurso = cursoPorCliente.remove(c.getCedula());
        if (idCurso != null) cursoService.liberarCupo(idCurso);
    }

    /* ---------- auxiliares ---------- */

    private Cliente buscar(String cedula) {
        Integer id = vacio(cedula) ? null : aEntero(cedula.trim());
        Cliente c = id == null ? null : clienteRepository.findById(id).orElse(null);
        if (c == null) throw new RecursoNoEncontradoException("una inscripción", cedula);
        return c;
    }

    private static void validarDatosPersonales(InscripcionDTO d, Map<String, String> errores) {
        requerido(errores, "nombre", d.nombre());
        longitud(errores, "nombre", d.nombre(), 3, 30);
        requerido(errores, "apellido1", d.apellido1());
        longitud(errores, "apellido1", d.apellido1(), 2, 30);
        requerido(errores, "apellido2", d.apellido2());
        longitud(errores, "apellido2", d.apellido2(), 2, 30);
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

    private static void copiarDatosPersonales(InscripcionDTO d, Cliente destino) {
        destino.setNombre(limpiar(d.nombre()));
        destino.setApellido1(limpiar(d.apellido1()));
        destino.setApellido2(limpiar(d.apellido2()));
        destino.setEmail(limpiar(d.email()));
        destino.setCelular(Integer.valueOf(limpiar(d.telefono()).replace("-", "")));
        destino.setFechaNacimiento(LocalDate.parse(d.fechaNacimiento().trim()));
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

    /** datosEnvio no nulo solo justo después de crear/actualizar, para echar tipoInscripcion/idCurso/objetivo. */
    private InscripcionRespuestaDTO aRespuesta(Cliente c, InscripcionDTO datosEnvio) {
        Integer edad = c.getFechaNacimiento() == null ? null : calcularEdad(c.getFechaNacimiento());
        String tipo = datosEnvio != null ? datosEnvio.tipoInscripcion() : null;
        Integer idCurso = datosEnvio != null ? aEntero(datosEnvio.idCurso()) : cursoPorCliente.get(c.getCedula());
        String objetivo = datosEnvio != null ? datosEnvio.objetivo() : null;
        return new InscripcionRespuestaDTO(String.valueOf(c.getCedula()), c.getNombre(), c.getApellido1(),
                c.getApellido2(), c.getEmail(), String.valueOf(c.getCelular()),
                c.getFechaNacimiento() == null ? null : c.getFechaNacimiento().toString(), edad,
                tipo, idCurso, objetivo, c.getFechaInscripcion().toString());
    }
}
