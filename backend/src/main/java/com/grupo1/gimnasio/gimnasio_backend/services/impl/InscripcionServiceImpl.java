package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.InscripcionRespuestaDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.models.Cliente;
import com.grupo1.gimnasio.gimnasio_backend.models.Credencial;
import com.grupo1.gimnasio.gimnasio_backend.observer.EventoInscripcion;
import com.grupo1.gimnasio.gimnasio_backend.observer.PublicadorInscripciones;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.CredencialRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.CursoRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.HistorialCursoRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.RutinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.CursoService;
import com.grupo1.gimnasio.gimnasio_backend.services.InscripcionService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Reglas de negocio de las inscripciones (se guardan en las tablas cliente y credenciales):
 *  - validación de nombre, cédula, correo, teléfono, fecha de nacimiento y contraseña;
 *  - cálculo de la edad a partir de la fecha de nacimiento (mínimo 14 años, igual que el frontend);
 *  - una sola inscripción por cédula (409 si se repite);
 *  - curso: debe existir y tener cupos (se reserva uno); rutina: objetivo válido;
 *  - al actualizar no se cambia la cédula (PK) ni el curso (FK);
 *  - al desinscribir se libera el cupo del curso; no se puede si el cliente tiene rutinas o historial;
 *  - la contraseña se guarda cifrada con BCrypt (hash + salt), nunca en texto plano;
 *  - el inicio de sesión de los tres roles está en AutenticacionServiceImpl.
 *
 * Patrón Singleton: bean de alcance singleton.
 * Patrón Observer: avisa a PublicadorInscripciones (Subject) en cada inscripción y desinscripción.
 * Patrón Iterator: listar() recorre los clientes con java.util.Iterator.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InscripcionServiceImpl implements InscripcionService {

    static final int EDAD_MINIMA = 14;
    private static final int LARGO_PARTE_NOMBRE = 30; // VARCHAR(30) de nombre, apellido_1 y apellido_2
    private static final Set<String> TIPOS = Set.of("rutina", "curso");
    private static final Set<String> OBJETIVOS = Set.of("fuerza", "resistencia", "peso", "flexibilidad");

    private final ClienteRepository clienteRepository;
    private final CredencialRepository credencialRepository;
    private final CursoRepository cursoRepository;
    private final RutinaRepository rutinaRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final CursoService cursoService;
    private final PublicadorInscripciones publicador;
    private final PasswordEncoder passwordEncoder;

    /** Inyección por constructor: Spring entrega el único bean de cada dependencia. */
    public InscripcionServiceImpl(ClienteRepository clienteRepository, CredencialRepository credencialRepository,
                                  CursoRepository cursoRepository, RutinaRepository rutinaRepository,
                                  HistorialCursoRepository historialCursoRepository, CursoService cursoService,
                                  PublicadorInscripciones publicador, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.credencialRepository = credencialRepository;
        this.cursoRepository = cursoRepository;
        this.rutinaRepository = rutinaRepository;
        this.historialCursoRepository = historialCursoRepository;
        this.cursoService = cursoService;
        this.publicador = publicador;
        this.passwordEncoder = passwordEncoder;
    }

    /** PATRÓN ITERATOR: recorre la colección de entidades Cliente con java.util.Iterator. */
    @Override
    @Transactional(readOnly = true)
    public List<InscripcionRespuestaDTO> listar() {
        List<InscripcionRespuestaDTO> lista = new ArrayList<>();
        Iterator<Cliente> iterador = clienteRepository.findAll(Sort.by("nombre", "apellido1")).iterator();
        while (iterador.hasNext()) {
            lista.add(aRespuesta(iterador.next()));
        }
        return lista;
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionRespuestaDTO obtener(String cedula) {
        return aRespuesta(buscar(cedula));
    }

    @Override
    @Transactional
    public InscripcionRespuestaDTO crear(InscripcionDTO datos) {
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

        Long cedula = Long.valueOf(datos.cedula().trim());
        if (clienteRepository.existsById(cedula)) {
            throw new ConflictoException("Ya existe una inscripción con la cédula " + cedula + ".");
        }
        if (idCurso != null) cursoService.reservarCupo(idCurso); // 404 o 409 si no se puede

        Cliente cliente = new Cliente();
        cliente.setCedula(cedula);
        cliente.setTipoInscripcion(datos.tipoInscripcion());
        cliente.setCursoInscrito(idCurso == null ? null : cursoRepository.getReferenceById((long) idCurso));
        cliente.setObjetivo("rutina".equals(datos.tipoInscripcion()) ? datos.objetivo() : null);
        cliente.setFechaInscripcion(LocalDate.now());
        cliente.setTelHabitacion(0);
        copiarDatosPersonales(datos, cliente);

        Credencial credencial = new Credencial();
        credencial.setCliente(cliente);
        credencial.setPasswordHash(passwordEncoder.encode(datos.password())); // BCrypt: hash + salt
        cliente.setCredencial(credencial);

        Cliente guardado = clienteRepository.save(cliente); // cascade: guarda también la credencial
        publicador.notificar(evento(EventoInscripcion.Tipo.INSCRIPCION, guardado));
        return aRespuesta(guardado);
    }

    @Override
    @Transactional
    public InscripcionRespuestaDTO actualizar(String cedula, InscripcionDTO cambios) {
        Cliente actual = buscar(cedula);

        if (cambios.cedula() != null && !cambios.cedula().trim().equals(String.valueOf(actual.getCedula()))) {
            throw new DatosInvalidosException("No se permite modificar la cédula (llave primaria).");
        }
        if (cambios.idCurso() != null && !Objects.equals(aEntero(cambios.idCurso()), idCurso(actual))) {
            throw new DatosInvalidosException(
                    "No se permite cambiar el curso (llave foránea). Desinscribite y volvé a inscribirte.");
        }
        if (cambios.tipoInscripcion() != null && !cambios.tipoInscripcion().equals(actual.getTipoInscripcion())) {
            throw new DatosInvalidosException("No se permite cambiar el tipo de inscripción.");
        }

        Map<String, String> errores = new LinkedHashMap<>();
        validarDatosPersonales(cambios, errores);
        if ("rutina".equals(actual.getTipoInscripcion()) && cambios.objetivo() != null
                && !OBJETIVOS.contains(cambios.objetivo())) {
            errores.put("objetivo", "Seleccioná un objetivo válido.");
        }
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        copiarDatosPersonales(cambios, actual);
        if ("rutina".equals(actual.getTipoInscripcion()) && cambios.objetivo() != null) {
            actual.setObjetivo(cambios.objetivo());
        }
        return aRespuesta(clienteRepository.save(actual));
    }

    @Override
    @Transactional
    public void eliminar(String cedula) {
        Cliente cliente = buscar(cedula);
        if (rutinaRepository.existsByCliente_Cedula(cliente.getCedula())
                || historialCursoRepository.existsByCliente_Cedula(cliente.getCedula())) {
            throw new ConflictoException("No se puede desinscribir al cliente " + cliente.getCedula()
                    + " porque tiene rutinas o historial de cursos registrados.");
        }
        Integer idCurso = idCurso(cliente);
        EventoInscripcion aviso = evento(EventoInscripcion.Tipo.DESINSCRIPCION, cliente);

        clienteRepository.delete(cliente); // orphanRemoval: borra también la credencial
        if (idCurso != null) cursoService.liberarCupo(idCurso);
        publicador.notificar(aviso);
    }

    /* ---------- auxiliares ---------- */

    private Cliente buscar(String cedula) {
        Long numero = null;
        if (!vacio(cedula)) {
            try {
                numero = Long.valueOf(cedula.trim());
            } catch (NumberFormatException e) {
                // cae en el 404 de abajo
            }
        }
        return (numero == null ? Optional.<Cliente>empty() : clienteRepository.findById(numero))
                .orElseThrow(() -> new RecursoNoEncontradoException("una inscripción", cedula));
    }

    private static void validarDatosPersonales(InscripcionDTO d, Map<String, String> errores) {
        requerido(errores, "nombre", d.nombre());
        longitud(errores, "nombre", d.nombre(), 3, 80);
        if (!vacio(d.nombre())) {
            for (String parte : separarNombre(d.nombre())) {
                if (parte.length() > LARGO_PARTE_NOMBRE) {
                    errores.putIfAbsent("nombre", "El nombre y cada apellido deben tener máximo "
                            + LARGO_PARTE_NOMBRE + " caracteres.");
                }
            }
        }
        requerido(errores, "email", d.email());
        patron(errores, "email", d.email(), EMAIL, "Correo electrónico inválido.");
        longitud(errores, "email", d.email(), 5, 100);
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

    /**
     * El formulario pide el nombre completo; la tabla cliente lo guarda en tres columnas.
     * Las dos últimas palabras son los apellidos y el resto el nombre ("María José Mora Solís").
     */
    private static String[] separarNombre(String completo) {
        String[] palabras = limpiar(completo).split("\\s+");
        int n = palabras.length;
        if (n == 1) return new String[]{palabras[0], "", ""};
        if (n == 2) return new String[]{palabras[0], palabras[1], ""};
        return new String[]{String.join(" ", Arrays.copyOfRange(palabras, 0, n - 2)),
                palabras[n - 2], palabras[n - 1]};
    }

    private static void copiarDatosPersonales(InscripcionDTO d, Cliente destino) {
        String[] partes = separarNombre(d.nombre());
        destino.setNombre(partes[0]);
        destino.setApellido1(partes[1]);
        destino.setApellido2(partes[2]);
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

    private static Integer idCurso(Cliente c) {
        return c.getCursoInscrito() == null ? null : c.getCursoInscrito().getIdCurso().intValue();
    }

    private static String nombreCompleto(Cliente c) {
        return String.join(" ", c.getNombre(), c.getApellido1(), c.getApellido2()).trim().replaceAll("\\s+", " ");
    }

    /** 88888888 → "8888-8888" (como lo escribe el usuario en el formulario). */
    private static String formatoTelefono(Integer celular) {
        if (celular == null || celular == 0) return null;
        String digitos = String.valueOf(celular);
        return digitos.length() == 8 ? digitos.substring(0, 4) + "-" + digitos.substring(4) : digitos;
    }

    private static EventoInscripcion evento(EventoInscripcion.Tipo tipo, Cliente c) {
        return new EventoInscripcion(tipo, String.valueOf(c.getCedula()), nombreCompleto(c),
                c.getTipoInscripcion(), idCurso(c), LocalDateTime.now());
    }

    private static InscripcionRespuestaDTO aRespuesta(Cliente c) {
        LocalDate nacimiento = c.getFechaNacimiento();
        return new InscripcionRespuestaDTO(String.valueOf(c.getCedula()), nombreCompleto(c), c.getEmail(),
                formatoTelefono(c.getCelular()),
                nacimiento == null ? null : nacimiento.toString(),
                nacimiento == null ? null : calcularEdad(nacimiento),
                c.getTipoInscripcion(), idCurso(c), c.getObjetivo(), c.getFechaInscripcion().toString());
    }
}
