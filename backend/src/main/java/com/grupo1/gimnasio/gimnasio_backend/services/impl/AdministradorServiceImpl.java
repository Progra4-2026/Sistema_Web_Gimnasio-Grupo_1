package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.AdministradorDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.models.Administrador;
import com.grupo1.gimnasio.gimnasio_backend.repositories.AdministradorRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.AdministradorService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.limpiar;
import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.vacio;

/**
 * Cuentas de administrador, gestionadas por otro administrador:
 *  - el código lo asigna la base de datos y es el usuario de inicio de sesión;
 *  - la contraseña se guarda cifrada con BCrypt;
 *  - no se puede borrar la propia cuenta ni al último administrador.
 *
 * Patrón Singleton: bean de alcance singleton.
 * Patrón Iterator: listar() recorre los administradores con java.util.Iterator.
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
    @Transactional(readOnly = true)
    public List<AdministradorDTO> listar() {
        List<AdministradorDTO> lista = new ArrayList<>();
        Iterator<Administrador> iterador = administradorRepository.findAll(Sort.by("codAdministrador")).iterator();
        while (iterador.hasNext()) {
            lista.add(aDto(iterador.next()));
        }
        return lista;
    }

    @Override
    @Transactional
    public AdministradorDTO crear(AdministradorDTO datos) {
        Administrador nuevo = new Administrador();
        nuevo.setNombre(limpiar(datos.nombre()));
        nuevo.setEmail(vacio(datos.email()) ? null : limpiar(datos.email()));
        nuevo.setPasswordHash(passwordEncoder.encode(datos.password()));
        return aDto(administradorRepository.save(nuevo));
    }

    @Override
    @Transactional
    public void eliminar(long codAdministrador, long codEnSesion) {
        Administrador admin = administradorRepository.findById(codAdministrador)
                .orElseThrow(() -> new RecursoNoEncontradoException("un administrador", codAdministrador));
        if (codAdministrador == codEnSesion) {
            throw new ConflictoException("No podés eliminar tu propia cuenta de administrador.");
        }
        if (administradorRepository.count() <= 1) {
            throw new ConflictoException("No se puede eliminar el último administrador del sistema.");
        }
        administradorRepository.delete(admin);
    }

    private static AdministradorDTO aDto(Administrador a) {
        return new AdministradorDTO(a.getCodAdministrador(), a.getNombre(), a.getEmail(), null);
    }
}
