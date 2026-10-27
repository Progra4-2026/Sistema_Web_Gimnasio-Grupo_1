package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.AdministradorDTO;
import com.grupo1.gimnasio.gimnasio_backend.entities.Administrador;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.repositories.AdministradorRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.AdministradorService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Alta/borrado de cuentas de administrador, a cargo de otro administrador. Mismo esquema
 * de contraseña por defecto que InstructorServiceImpl.
 *
 * Patrón Singleton: bean de alcance singleton. Persistencia real en MySQL vía AdministradorRepository.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorServiceImpl(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AdministradorDTO> listar() {
        return administradorRepository.findAll().stream()
                .sorted(Comparator.comparing(Administrador::getCodAdministrador))
                .map(AdministradorServiceImpl::aDto)
                .toList();
    }

    @Override
    public synchronized AdministradorDTO crear(AdministradorDTO datos) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "codAdministrador",
                datos.codAdministrador() == null ? null : datos.codAdministrador().toString());
        requerido(errores, "nombre", datos.nombre());
        longitud(errores, "nombre", datos.nombre(), 2, 30);
        patron(errores, "email", datos.email(), EMAIL, "Correo electrónico inválido.");
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        if (administradorRepository.existsById(datos.codAdministrador())) {
            throw new ConflictoException("Ya existe un administrador con el código " + datos.codAdministrador() + ".");
        }

        Administrador nuevo = new Administrador();
        nuevo.setCodAdministrador(datos.codAdministrador());
        nuevo.setNombre(limpiar(datos.nombre()));
        nuevo.setEmail(limpiar(datos.email()));
        String password = vacio(datos.password()) ? String.valueOf(datos.codAdministrador()) : datos.password();
        nuevo.setContrasena(passwordEncoder.encode(password));
        administradorRepository.save(nuevo);
        return aDto(nuevo);
    }

    @Override
    public synchronized void eliminar(int codAdministrador) {
        Administrador admin = administradorRepository.findById(codAdministrador)
                .orElseThrow(() -> new RecursoNoEncontradoException("un administrador", codAdministrador));
        if (administradorRepository.count() <= 1) {
            throw new ConflictoException("No se puede eliminar el último administrador del sistema.");
        }
        administradorRepository.delete(admin);
    }

    private static AdministradorDTO aDto(Administrador a) {
        return new AdministradorDTO(a.getCodAdministrador(), a.getNombre(), a.getEmail(), null);
    }
}
