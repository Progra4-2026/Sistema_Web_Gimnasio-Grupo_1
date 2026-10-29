package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.InstructorDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.models.Instructor;
import com.grupo1.gimnasio.gimnasio_backend.repositories.HistorialCursoRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.InstructorRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.RutinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.InstructorService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.limpiar;
import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.vacio;

/**
 * Gestión de instructores, a cargo del administrador:
 *  - el código lo asigna la base de datos y es el usuario de inicio de sesión;
 *  - la contraseña es obligatoria al crear y se guarda cifrada con BCrypt; al actualizar solo
 *    cambia si se envía una nueva;
 *  - no se puede borrar un instructor que tiene rutinas o historial de cursos.
 *
 * Patrón Singleton: bean de alcance singleton.
 * Patrón Iterator: listar() recorre los instructores con java.util.Iterator.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final RutinaRepository rutinaRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final PasswordEncoder passwordEncoder;

    public InstructorServiceImpl(InstructorRepository instructorRepository, RutinaRepository rutinaRepository,
                                 HistorialCursoRepository historialCursoRepository, PasswordEncoder passwordEncoder) {
        this.instructorRepository = instructorRepository;
        this.rutinaRepository = rutinaRepository;
        this.historialCursoRepository = historialCursoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorDTO> listar() {
        List<InstructorDTO> lista = new ArrayList<>();
        Iterator<Instructor> iterador = instructorRepository.findAll(Sort.by("codInstructor")).iterator();
        while (iterador.hasNext()) {
            lista.add(aDto(iterador.next()));
        }
        return lista;
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorDTO obtener(long codInstructor) {
        return aDto(buscar(codInstructor));
    }

    @Override
    @Transactional
    public InstructorDTO crear(InstructorDTO datos) {
        if (vacio(datos.password())) {
            throw new DatosInvalidosException(Map.of("password", "Este campo es obligatorio."));
        }
        Instructor nuevo = new Instructor();
        copiarDatos(datos, nuevo);
        nuevo.setPasswordHash(passwordEncoder.encode(datos.password()));
        return aDto(instructorRepository.save(nuevo));
    }

    @Override
    @Transactional
    public InstructorDTO actualizar(long codInstructor, InstructorDTO cambios) {
        Instructor actual = buscar(codInstructor);
        if (cambios.codInstructor() != null && cambios.codInstructor() != codInstructor) {
            throw new DatosInvalidosException("No se permite modificar el código del instructor.");
        }
        copiarDatos(cambios, actual);
        if (!vacio(cambios.password())) actual.setPasswordHash(passwordEncoder.encode(cambios.password()));
        return aDto(instructorRepository.save(actual));
    }

    @Override
    @Transactional
    public void eliminar(long codInstructor) {
        Instructor instructor = buscar(codInstructor);
        if (rutinaRepository.existsByInstructor_CodInstructor(codInstructor)
                || historialCursoRepository.existsByInstructor_CodInstructor(codInstructor)) {
            throw new ConflictoException("No se puede eliminar al instructor " + instructor.nombreCompleto()
                    + " porque tiene rutinas o historial de cursos registrados.");
        }
        instructorRepository.delete(instructor);
    }

    /* ---------- auxiliares ---------- */

    private Instructor buscar(long codInstructor) {
        return instructorRepository.findById(codInstructor)
                .orElseThrow(() -> new RecursoNoEncontradoException("un instructor", codInstructor));
    }

    private static void copiarDatos(InstructorDTO d, Instructor destino) {
        LocalDate contratacion;
        try {
            contratacion = LocalDate.parse(d.fechaContratacion().trim());
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException(Map.of("fechaContratacion", "Usá el formato AAAA-MM-DD."));
        }
        if (contratacion.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException(Map.of("fechaContratacion", "La fecha no puede ser futura."));
        }
        destino.setNombre(limpiar(d.nombre()));
        destino.setApellido1(limpiar(d.apellido1()));
        destino.setApellido2(limpiar(d.apellido2()));
        destino.setDireccion(vacio(d.direccion()) ? null : limpiar(d.direccion()));
        destino.setEmail(vacio(d.email()) ? null : limpiar(d.email()));
        destino.setTelCel(d.telCel());
        if (destino.getTelHabitacion() == null) destino.setTelHabitacion(0);
        destino.setFechaContratacion(contratacion);
    }

    private static InstructorDTO aDto(Instructor i) {
        return new InstructorDTO(i.getCodInstructor(), i.getNombre(), i.getApellido1(), i.getApellido2(),
                i.getDireccion(), i.getEmail(), i.getTelCel(),
                i.getFechaContratacion() == null ? null : i.getFechaContratacion().toString(), null);
    }
}
