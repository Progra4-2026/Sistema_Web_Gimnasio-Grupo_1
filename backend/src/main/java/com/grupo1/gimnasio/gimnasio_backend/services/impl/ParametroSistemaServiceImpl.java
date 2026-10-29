package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.ParametroSistemaDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.models.ParametroSistema;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ParametroSistemaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.ParametroSistemaService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.limpiar;

/**
 * Parámetros del sistema (clave/valor) que gestiona el administrador. Las claves las crea
 * GIMNASIO.sql; desde la API solo se actualiza el valor de una clave existente.
 *
 * Patrón Singleton: bean de alcance singleton.
 * Patrón Iterator: listar() recorre los parámetros con java.util.Iterator.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ParametroSistemaServiceImpl implements ParametroSistemaService {

    private final ParametroSistemaRepository parametroSistemaRepository;

    public ParametroSistemaServiceImpl(ParametroSistemaRepository parametroSistemaRepository) {
        this.parametroSistemaRepository = parametroSistemaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametroSistemaDTO> listar() {
        List<ParametroSistemaDTO> lista = new ArrayList<>();
        Iterator<ParametroSistema> iterador = parametroSistemaRepository.findAll(Sort.by("clave")).iterator();
        while (iterador.hasNext()) {
            lista.add(aDto(iterador.next()));
        }
        return lista;
    }

    @Override
    @Transactional
    public ParametroSistemaDTO actualizar(String clave, ParametroSistemaDTO cambios) {
        ParametroSistema actual = parametroSistemaRepository.findById(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("un parámetro del sistema", clave));
        if (cambios.clave() != null && !cambios.clave().equals(clave)) {
            throw new DatosInvalidosException("No se permite modificar la clave del parámetro (llave primaria).");
        }
        String valor = limpiar(cambios.valor());
        // Un parámetro numérico (capacidad, horas...) debe seguir siendo un número entero positivo
        if (actual.getValor().matches("\\d+") && !valor.matches("[1-9]\\d{0,8}")) {
            throw new DatosInvalidosException(java.util.Map.of("valor", "Este parámetro debe ser un número entero mayor que 0."));
        }
        actual.setValor(valor);
        return aDto(parametroSistemaRepository.save(actual));
    }

    private static ParametroSistemaDTO aDto(ParametroSistema p) {
        return new ParametroSistemaDTO(p.getClave(), p.getValor(), p.getDescripcion());
    }
}
