package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.ParametroSistemaDTO;
import com.grupo1.gimnasio.gimnasio_backend.entities.ParametroSistema;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ParametroSistemaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.ParametroSistemaService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.limpiar;
import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.requerido;

/**
 * Parámetros del sistema (clave/valor) que gestiona el administrador. Las claves las crea
 * el script de base de datos (database/GIMNASIO.sql); desde la API solo se actualiza el valor
 * de una clave existente, no se crean ni borran claves.
 *
 * Patrón Singleton: bean de alcance singleton. Persistencia real en MySQL vía ParametroSistemaRepository.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ParametroSistemaServiceImpl implements ParametroSistemaService {

    private final ParametroSistemaRepository parametroSistemaRepository;

    public ParametroSistemaServiceImpl(ParametroSistemaRepository parametroSistemaRepository) {
        this.parametroSistemaRepository = parametroSistemaRepository;
    }

    @Override
    public List<ParametroSistemaDTO> listar() {
        return parametroSistemaRepository.findAll().stream()
                .sorted(Comparator.comparing(ParametroSistema::getClave))
                .map(ParametroSistemaServiceImpl::aDto)
                .toList();
    }

    @Override
    public synchronized ParametroSistemaDTO actualizar(String clave, ParametroSistemaDTO cambios) {
        ParametroSistema actual = parametroSistemaRepository.findById(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("un parámetro del sistema", clave));

        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "valor", cambios.valor());
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        actual.setValor(limpiar(cambios.valor()));
        parametroSistemaRepository.save(actual);
        return aDto(actual);
    }

    private static ParametroSistemaDTO aDto(ParametroSistema p) {
        return new ParametroSistemaDTO(p.getClave(), p.getValor(), p.getDescripcion());
    }
}
