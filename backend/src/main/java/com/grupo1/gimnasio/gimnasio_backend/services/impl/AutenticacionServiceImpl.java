package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.LoginDTO;
import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;
import com.grupo1.gimnasio.gimnasio_backend.entities.Administrador;
import com.grupo1.gimnasio.gimnasio_backend.entities.Cliente;
import com.grupo1.gimnasio.gimnasio_backend.entities.Instructor;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.NoAutenticadoException;
import com.grupo1.gimnasio.gimnasio_backend.repositories.AdministradorRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.InstructorRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.AutenticacionService;
import com.grupo1.gimnasio.gimnasio_backend.sesion.GestorSesiones;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Manejo de sesiones con HttpSession + Spring Security:
 *  - login: valida credenciales probando cliente, instructor y administrador en ese orden
 *    (mismo mensaje de error sin importar cuál de los dos datos falló, o si el id no existe
 *    en ninguna de las 3 tablas); renueva el id de sesión (evita fijación de sesión), guarda
 *    el usuario en la sesión, la registra en el Singleton GestorSesiones, y además construye
 *    el SecurityContext de Spring Security (rol ROLE_CLIENTE/ROLE_INSTRUCTOR/ROLE_ADMINISTRADOR)
 *    para que @PreAuthorize funcione en los controladores;
 *  - la sesión expira tras 30 minutos sin actividad;
 *  - logout: invalida la sesión, la quita del registro y limpia el SecurityContext.
 *
 * Patrón Singleton: bean de alcance singleton + uso de GestorSesiones.getInstancia().
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AutenticacionServiceImpl implements AutenticacionService {

    private static final int MINUTOS_INACTIVIDAD = 30;
    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Identificador o contraseña incorrectos.";

    private final ClienteRepository clienteRepository;
    private final InstructorRepository instructorRepository;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;
    private final GestorSesiones gestorSesiones = GestorSesiones.getInstancia();

    public AutenticacionServiceImpl(ClienteRepository clienteRepository, InstructorRepository instructorRepository,
                                     AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder,
                                     SecurityContextRepository securityContextRepository) {
        this.clienteRepository = clienteRepository;
        this.instructorRepository = instructorRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public UsuarioSesionDTO iniciarSesion(LoginDTO credenciales, HttpServletRequest peticion,
                                           HttpServletResponse respuesta) {
        if (credenciales == null) throw new NoAutenticadoException(MENSAJE_CREDENCIALES_INVALIDAS);
        UsuarioSesionDTO usuario = autenticar(credenciales);

        // Si ya había una sesión, se quita del registro antes de renovar su id
        HttpSession anterior = peticion.getSession(false);
        if (anterior != null) gestorSesiones.eliminar(anterior.getId());

        HttpSession sesion = peticion.getSession(true);
        peticion.changeSessionId();
        sesion.setAttribute(ATRIBUTO_USUARIO, usuario);
        sesion.setMaxInactiveInterval(MINUTOS_INACTIVIDAD * 60);
        gestorSesiones.registrar(sesion.getId(), usuario);

        List<SimpleGrantedAuthority> permisos = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.rol().toUpperCase()));
        Authentication autenticacion = new UsernamePasswordAuthenticationToken(usuario.cedula(), null, permisos);
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

    /** Prueba cliente, instructor y administrador en ese orden; mismo mensaje de error en cualquier caso. */
    private UsuarioSesionDTO autenticar(LoginDTO credenciales) {
        Integer id = aEntero(credenciales.cedula());
        String password = credenciales.password();
        if (id == null || password == null || password.isBlank()) {
            throw new NoAutenticadoException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent() && passwordEncoder.matches(password, cliente.get().getContrasena())) {
            Cliente c = cliente.get();
            return new UsuarioSesionDTO(String.valueOf(c.getCedula()), c.nombreCompleto(), "cliente");
        }

        Optional<Instructor> instructor = instructorRepository.findById(id);
        if (instructor.isPresent() && passwordEncoder.matches(password, instructor.get().getContrasena())) {
            Instructor i = instructor.get();
            return new UsuarioSesionDTO(String.valueOf(i.getCodInstructor()), i.nombreCompleto(), "instructor");
        }

        Optional<Administrador> administrador = administradorRepository.findById(id);
        if (administrador.isPresent() && passwordEncoder.matches(password, administrador.get().getContrasena())) {
            Administrador a = administrador.get();
            return new UsuarioSesionDTO(String.valueOf(a.getCodAdministrador()), a.getNombre(), "administrador");
        }

        throw new NoAutenticadoException(MENSAJE_CREDENCIALES_INVALIDAS);
    }

    private static Integer aEntero(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
