package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.InstructorDTO;
import com.grupo1.gimnasio.gimnasio_backend.entities.Instructor;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.repositories.InstructorRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.InstructorService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Alta/edición/borrado de instructores, a cargo del administrador. La contraseña se cifra
 * con BCrypt (PasswordEncoder); si no se envía al crear, queda el propio código como
 * contraseña por defecto, igual que CargaInicialDatosRunner hace con los datos sembrados.
 *
 * Patrón Singleton: bean de alcance singleton. Persistencia real en MySQL vía InstructorRepository.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;

    public InstructorServiceImpl(InstructorRepository instructorRepository, PasswordEncoder passwordEncoder) {
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<InstructorDTO> listar() {
        return instructorRepository.findAll().stream()
                .sorted(Comparator.comparing(Instructor::getCodInstructor))
                .map(InstructorServiceImpl::aDto)
                .toList();
    }

    @Override
    public InstructorDTO obtener(int codInstructor) {
        return aDto(buscar(codInstructor));
    }

    @Override
    public synchronized InstructorDTO crear(InstructorDTO datos) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "codInstructor", datos.codInstructor() == null ? null : datos.codInstructor().toString());
        LocalDate fechaContratacion = validarDatos(datos, errores);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        if (instructorRepository.existsById(datos.codInstructor())) {
            throw new ConflictoException("Ya existe un instructor con el código " + datos.codInstructor() + ".");
        }

        Instructor nuevo = new Instructor();
        nuevo.setCodInstructor(datos.codInstructor());
        copiarDatos(datos, nuevo);
        nuevo.setFechaContratacion(fechaContratacion);
        String password = vacio(datos.password()) ? String.valueOf(datos.codInstructor()) : datos.password();
        nuevo.setContrasena(passwordEncoder.encode(password));
        instructorRepository.save(nuevo);
        return aDto(nuevo);
    }

    @Override
    public synchronized InstructorDTO actualizar(int codInstructor, InstructorDTO cambios) {
        Instructor actual = buscar(codInstructor);
        Map<String, String> errores = new LinkedHashMap<>();
        LocalDate fechaContratacion = validarDatos(cambios, errores);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        copiarDatos(cambios, actual);
        actual.setFechaContratacion(fechaContratacion);
        if (!vacio(cambios.password())) actual.setContrasena(passwordEncoder.encode(cambios.password()));
        instructorRepository.save(actual);
        return aDto(actual);
    }

    @Override
    public synchronized void eliminar(int codInstructor) {
        instructorRepository.delete(buscar(codInstructor));
    }

    private Instructor buscar(int codInstructor) {
        return instructorRepository.findById(codInstructor)
                .orElseThrow(() -> new RecursoNoEncontradoException("un instructor", codInstructor));
    }

    private static LocalDate validarDatos(InstructorDTO d, Map<String, String> errores) {
        requerido(errores, "nombre", d.nombre());
        longitud(errores, "nombre", d.nombre(), 2, 30);
        requerido(errores, "apellido1", d.apellido1());
        longitud(errores, "apellido1", d.apellido1(), 2, 30);
        requerido(errores, "apellido2", d.apellido2());
        longitud(errores, "apellido2", d.apellido2(), 2, 30);
        patron(errores, "email", d.email(), EMAIL, "Correo electrónico inválido.");
        requerido(errores, "fechaContratacion", d.fechaContratacion());
        LocalDate fecha = null;
        if (!vacio(d.fechaContratacion())) {
            try {
                fecha = LocalDate.parse(d.fechaContratacion().trim());
            } catch (DateTimeParseException e) {
                errores.put("fechaContratacion", "Usá el formato AAAA-MM-DD.");
            }
        }
        return fecha;
    }

    private static void copiarDatos(InstructorDTO d, Instructor destino) {
        destino.setNombre(limpiar(d.nombre()));
        destino.setApellido1(limpiar(d.apellido1()));
        destino.setApellido2(limpiar(d.apellido2()));
        destino.setDireccion(limpiar(d.direccion()));
        destino.setEmail(limpiar(d.email()));
        destino.setTelCel(d.telCel() == null ? 0 : d.telCel());
        destino.setTelHabitacion(d.telHabitacion() == null ? 0 : d.telHabitacion());
    }

    private static InstructorDTO aDto(Instructor i) {
        return new InstructorDTO(i.getCodInstructor(), i.getNombre(), i.getApellido1(), i.getApellido2(),
                i.getDireccion(), i.getEmail(), i.getTelCel(), i.getTelHabitacion(),
                i.getFechaContratacion() == null ? null : i.getFechaContratacion().toString(), null);
    }
}
