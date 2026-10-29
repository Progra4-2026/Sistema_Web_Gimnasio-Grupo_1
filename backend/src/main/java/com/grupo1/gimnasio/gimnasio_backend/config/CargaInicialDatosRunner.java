package com.grupo1.gimnasio.gimnasio_backend.config;

import com.grupo1.gimnasio.gimnasio_backend.models.Administrador;
import com.grupo1.gimnasio.gimnasio_backend.models.Cliente;
import com.grupo1.gimnasio.gimnasio_backend.models.Credencial;
import com.grupo1.gimnasio.gimnasio_backend.models.Instructor;
import com.grupo1.gimnasio.gimnasio_backend.repositories.AdministradorRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.CredencialRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.InstructorRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

/**
 * Deja listo el login de demostración sin escribir hashes BCrypt a mano en GIMNASIO.sql.
 * Corre una vez en cada arranque, cuando la base de datos ya está disponible:
 *  - a cada cliente de prueba sin credencial le crea una, con su propia cédula como contraseña;
 *  - a cada instructor sin contraseña le asigna su propio código como contraseña;
 *  - si no hay ningún administrador, crea uno de demostración.
 * Todas las contraseñas se guardan cifradas con BCrypt. Solo completa lo que falta: nunca
 * reemplaza una contraseña existente (por ejemplo, la de un cliente que se inscribió en la web).
 */
@Component
public class CargaInicialDatosRunner implements ApplicationRunner {

    private static final Logger LOG = Logger.getLogger(CargaInicialDatosRunner.class.getName());
    private static final String ADMIN_DEMO_PASSWORD = "admin123";

    private final ClienteRepository clienteRepository;
    private final CredencialRepository credencialRepository;
    private final InstructorRepository instructorRepository;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicialDatosRunner(ClienteRepository clienteRepository, CredencialRepository credencialRepository,
                                   InstructorRepository instructorRepository,
                                   AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.credencialRepository = credencialRepository;
        this.instructorRepository = instructorRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int clientes = 0;
        for (Cliente cliente : clienteRepository.findAll()) {
            if (!credencialRepository.existsById(cliente.getCedula())) {
                Credencial credencial = new Credencial();
                credencial.setCliente(cliente);
                credencial.setPasswordHash(passwordEncoder.encode(String.valueOf(cliente.getCedula())));
                credencialRepository.save(credencial);
                clientes++;
            }
        }

        int instructores = 0;
        for (Instructor instructor : instructorRepository.findAll()) {
            if (instructor.getPasswordHash() == null || instructor.getPasswordHash().isBlank()) {
                instructor.setPasswordHash(passwordEncoder.encode(String.valueOf(instructor.getCodInstructor())));
                instructorRepository.save(instructor);
                instructores++;
            }
        }

        if (administradorRepository.count() == 0) {
            Administrador admin = new Administrador();
            admin.setNombre("Administrador");
            admin.setEmail("admin@powerfit.cr");
            admin.setPasswordHash(passwordEncoder.encode(ADMIN_DEMO_PASSWORD));
            Administrador guardado = administradorRepository.save(admin);
            LOG.info(() -> "Administrador de demostración creado -> usuario: " + guardado.getCodAdministrador()
                    + " / contraseña: " + ADMIN_DEMO_PASSWORD);
        }

        int c = clientes, i = instructores;
        if (c + i > 0) {
            LOG.info(() -> "Contraseñas de demostración asignadas: " + c + " cliente(s) y " + i + " instructor(es).");
        }
    }
}
