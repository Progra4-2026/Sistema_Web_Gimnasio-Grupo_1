package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.LoginDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.NoAutenticadoException;
import com.grupo1.gimnasio.gimnasio_backend.models.Administrador;
import com.grupo1.gimnasio.gimnasio_backend.models.Cliente;
import com.grupo1.gimnasio.gimnasio_backend.models.Credencial;
import com.grupo1.gimnasio.gimnasio_backend.models.Instructor;
import com.grupo1.gimnasio.gimnasio_backend.repositories.AdministradorRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.CredencialRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.InstructorRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.AutenticacionService;
import com.grupo1.gimnasio.gimnasio_backend.sesion.GestorSesiones;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Inicio de sesión de los tres roles con HttpSession + Spring Security:
 *  - se prueba el identificador como cliente (cédula), instructor (código) y administrador
 *    (código), en ese orden; la contraseña se compara con el hash BCrypt guardado;
 *  - mismo mensaje de error si el usuario no existe o la contraseña es incorrecta, para no
 *    revelar cuál de los dos falló;
 *  - renueva el id de sesión (evita fijación de sesión), guarda el usuario en la sesión, lo
 *    registra en el Singleton GestorSesiones y construye el SecurityContext con el rol
 *    (ROLE_CLIENTE, ROLE_INSTRUCTOR o ROLE_ADMINISTRADOR) para que funcione @PreAuthorize;
 *  - la sesión expira tras 30 minutos sin actividad; logout la invalida.
 *
 * Patrón Singleton: bean de alcance singleton + uso de GestorSesiones.getInstancia().
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AutenticacionServiceImpl implements AutenticacionService {

    private static final int MINUTOS_INACTIVIDAD = 30;
    private static final String CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos.";
    private static final Pattern IDENTIFICADOR = Pattern.compile("^[0-9]{1,12}$");

    private final CredencialRepository credencialRepository;
    private final InstructorRepository instructorRepository;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;
    private final GestorSesiones gestorSesiones = GestorSesiones.getInstancia();

    public AutenticacionServiceImpl(CredencialRepository credencialRepository, InstructorRepository instructorRepository,
                                    AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder,
                                    SecurityContextRepository securityContextRepository) {
        this.credencialRepository = credencialRepository;
        this.instructorRepository = instructorRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioSesionDTO iniciarSesion(LoginDTO credenciales, HttpServletRequest peticion,
                                          HttpServletResponse respuesta) {
        UsuarioSesionDTO usuario = autenticar(credenciales);

        // Si ya había una sesión, se quita del registro antes de renovar su id
        HttpSession anterior = peticion.getSession(false);
        if (anterior != null) gestorSesiones.eliminar(anterior.getId());

        HttpSession sesion = peticion.getSession(true);
        peticion.changeSessionId();
        sesion.setAttribute(ATRIBUTO_USUARIO, usuario);
        sesion.setMaxInactiveInterval(MINUTOS_INACTIVIDAD * 60);
        gestorSesiones.registrar(sesion.getId(), usuario);

        // Spring Security: el nombre de la autenticación es la cédula/código; el permiso es el rol
        var permisos = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol().toUpperCase()));
        var autenticacion = new UsernamePasswordAuthenticationToken(usuario.cedula(), null, permisos);
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacion);
        SecurityContextHolder.setContext(contexto);
        securityContextRepository.saveContext(contexto, peticion, respuesta);

        return usuario;
    }

    @Override
    public void cerrarSesion(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            gestorSesiones.eliminar(sesion.getId());
            sesion.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    public UsuarioSesionDTO usuarioActual(HttpSession sesion) {
        Object usuario = sesion == null ? null : sesion.getAttribute(ATRIBUTO_USUARIO);
        if (usuario instanceof UsuarioSesionDTO u) return u;
        throw new NoAutenticadoException("Iniciá sesión para continuar.");
    }

    @Override
    public int sesionesActivas() {
        return gestorSesiones.cantidadActivas();
    }

    /** Prueba cliente, instructor y administrador, en ese orden. */
    private UsuarioSesionDTO autenticar(LoginDTO credenciales) {
        String texto = credenciales == null || credenciales.cedula() == null ? "" : credenciales.cedula().trim();
        String password = credenciales == null ? null : credenciales.password();
        if (!IDENTIFICADOR.matcher(texto).matches() || password == null || password.isBlank()) {
            throw new NoAutenticadoException(CREDENCIALES_INVALIDAS);
        }
        long id = Long.parseLong(texto);

        Credencial credencial = credencialRepository.findById(id).orElse(null);
        if (credencial != null && coincide(password, credencial.getPasswordHash())) {
            Cliente c = credencial.getCliente();
            String nombre = String.join(" ", c.getNombre(), c.getApellido1(), c.getApellido2())
                    .trim().replaceAll("\\s+", " ");
            return new UsuarioSesionDTO(String.valueOf(id), nombre, "cliente");
        }

        Instructor instructor = instructorRepository.findById(id).orElse(null);
        if (instructor != null && coincide(password, instructor.getPasswordHash())) {
            return new UsuarioSesionDTO(String.valueOf(id), instructor.nombreCompleto(), "instructor");
        }

        Administrador admin = administradorRepository.findById(id).orElse(null);
        if (admin != null && coincide(password, admin.getPasswordHash())) {
            return new UsuarioSesionDTO(String.valueOf(id), admin.getNombre(), "administrador");
        }

        throw new NoAutenticadoException(CREDENCIALES_INVALIDAS);
    }

    private boolean coincide(String password, String hash) {
        return hash != null && !hash.isBlank() && passwordEncoder.matches(password, hash);
    }
}
