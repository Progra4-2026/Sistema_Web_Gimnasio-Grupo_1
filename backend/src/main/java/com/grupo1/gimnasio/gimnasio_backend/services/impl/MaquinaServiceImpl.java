package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.MaquinaDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.ConflictoException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.models.Maquina;
import com.grupo1.gimnasio.gimnasio_backend.repositories.MaquinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.RutinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.MaquinaService;
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
 * Reglas de negocio de las máquinas (las gestiona el instructor):
 *  - descripción obligatoria (3-50) y estado opcional (validados con Bean Validation en MaquinaDTO);
 *  - el id lo asigna la base de datos y no se puede modificar;
 *  - no se puede borrar una máquina que está asignada en rutinas.
 *
 * Patrón Singleton: bean de alcance singleton.
 * Patrón Iterator: listar() recorre las máquinas con java.util.Iterator.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MaquinaServiceImpl implements MaquinaService {

    private final MaquinaRepository maquinaRepository;
    private final RutinaRepository rutinaRepository;

    public MaquinaServiceImpl(MaquinaRepository maquinaRepository, RutinaRepository rutinaRepository) {
        this.maquinaRepository = maquinaRepository;
        this.rutinaRepository = rutinaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaDTO> listar() {
        List<MaquinaDTO> lista = new ArrayList<>();
        Iterator<Maquina> iterador = maquinaRepository.findAll(Sort.by("idMaquina")).iterator();
        while (iterador.hasNext()) {
            lista.add(aDto(iterador.next()));
        }
        return lista;
    }

    @Override
    @Transactional(readOnly = true)
    public MaquinaDTO obtener(long id) {
        return aDto(buscar(id));
    }

    @Override
    @Transactional
    public MaquinaDTO crear(MaquinaDTO datos) {
        Maquina nueva = new Maquina();
        nueva.setDescripcion(limpiar(datos.descripcion()));
        nueva.setEstado(limpiar(datos.estado()));
        return aDto(maquinaRepository.save(nueva));
    }

    @Override
    @Transactional
    public MaquinaDTO actualizar(long id, MaquinaDTO cambios) {
        Maquina actual = buscar(id);
        if (cambios.id() != null && cambios.id() != id) {
            throw new DatosInvalidosException("No se permite modificar el identificador de la máquina.");
        }
        actual.setDescripcion(limpiar(cambios.descripcion()));
        actual.setEstado(limpiar(cambios.estado()));
        return aDto(maquinaRepository.save(actual));
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        Maquina maquina = buscar(id);
        if (rutinaRepository.existsByMaquina_IdMaquina(id)) {
            throw new ConflictoException("No se puede eliminar la máquina \"" + maquina.getDescripcion()
                    + "\" porque está asignada en rutinas. Eliminá o cambiá esas rutinas primero.");
        }
        maquinaRepository.delete(maquina);
    }

    private Maquina buscar(long id) {
        return maquinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("una máquina", id));
    }

    private static MaquinaDTO aDto(Maquina m) {
        return new MaquinaDTO(m.getIdMaquina(), m.getDescripcion(), m.getEstado());
    }
}
