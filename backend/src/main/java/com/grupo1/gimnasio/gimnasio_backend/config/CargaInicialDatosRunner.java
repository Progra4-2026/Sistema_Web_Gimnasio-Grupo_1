package com.grupo1.gimnasio.gimnasio_backend.config;

import com.grupo1.gimnasio.gimnasio_backend.entities.Administrador;
import com.grupo1.gimnasio.gimnasio_backend.entities.Cliente;
import com.grupo1.gimnasio.gimnasio_backend.entities.Instructor;
import com.grupo1.gimnasio.gimnasio_backend.repositories.AdministradorRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.InstructorRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Deja listo el login de demo sin tener que calcular hashes BCrypt a mano en GIMNASIO.sql:
 *  - a cualquier cliente/instructor sembrado con "contrasena" en blanco, le pone como
 *    contraseña por defecto su propia cédula/código (hasheada con BCrypt) — solo toca filas
 *    en blanco, así que no pisa contraseñas ya puestas por un registro real;
 *  - si no existe ningún administrador, crea uno de demo e imprime su contraseña en
 *    consola una sola vez (nunca se guarda en texto plano, solo se muestra para el login).
 * Corre una vez por arranque, después de que el datasource/JPA ya están listos.
 */
@Component
public class CargaInicialDatosRunner implements ApplicationRunner {

    private static final Logger LOG = Logger.getLogger(CargaInicialDatosRunner.class.getName());
    private static final int ADMIN_SEMILLA_COD = 1;
    private static final String ADMIN_SEMILLA_PASSWORD = "admin123";

    private final ClienteRepository clienteRepository;
    private final InstructorRepository instructorRepository;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicialDatosRunner(ClienteRepository clienteRepository, InstructorRepository instructorRepository,
                                    AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.instructorRepository = instructorRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (Cliente c : clienteRepository.findAll()) {
            if (c.getContrasena() == null || c.getContrasena().isBlank()) {
                c.setContrasena(passwordEncoder.encode(String.valueOf(c.getCedula())));
                clienteRepository.save(c);
            }
        }
        for (Instructor i : instructorRepository.findAll()) {
            if (i.getContrasena() == null || i.getContrasena().isBlank()) {
                i.setContrasena(passwordEncoder.encode(String.valueOf(i.getCodInstructor())));
                instructorRepository.save(i);
            }
        }
        if (administradorRepository.count() == 0) {
            Administrador admin = new Administrador();
            admin.setCodAdministrador(ADMIN_SEMILLA_COD);
            admin.setNombre("Administrador");
            admin.setEmail("admin@powerfit.com");
            admin.setContrasena(passwordEncoder.encode(ADMIN_SEMILLA_PASSWORD));
            administradorRepository.save(admin);
            LOG.info(() -> "Administrador de demo creado -> código: " + ADMIN_SEMILLA_COD
                    + " / contraseña: " + ADMIN_SEMILLA_PASSWORD);
        }
    }
}
