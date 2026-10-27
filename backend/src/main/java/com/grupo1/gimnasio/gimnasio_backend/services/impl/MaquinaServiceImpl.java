package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.MaquinaDTO;
import com.grupo1.gimnasio.gimnasio_backend.entities.Maquina;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.repositories.MaquinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.MaquinaService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Reglas de negocio de las máquinas (gestionadas por el instructor): descripción obligatoria
 * (3-50 caracteres, igual que VARCHAR(50) en la tabla), estado opcional. El id lo asigna el
 * servidor (igual que en la tabla "maquinas" no hay autoincremento).
 *
 * Patrón Singleton: bean de alcance singleton. Persistencia real en MySQL vía MaquinaRepository.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MaquinaServiceImpl implements MaquinaService {

    private final MaquinaRepository maquinaRepository;

    public MaquinaServiceImpl(MaquinaRepository maquinaRepository) {
        this.maquinaRepository = maquinaRepository;
    }

    @Override
    public List<MaquinaDTO> listar() {
        return maquinaRepository.findAll().stream()
                .sorted(Comparator.comparing(Maquina::getIdMaquina))
                .map(MaquinaServiceImpl::aDto)
                .toList();
    }

    @Override
    public MaquinaDTO obtener(int id) {
        return aDto(buscar(id));
    }

    @Override
    public synchronized MaquinaDTO crear(MaquinaDTO maquina) {
        validar(maquina);
        int id = maquinaRepository.findAll().stream().mapToInt(Maquina::getIdMaquina).max().orElse(0) + 1;

        Maquina nueva = new Maquina();
        nueva.setIdMaquina(id);
        nueva.setDescripcion(limpiar(maquina.descripcion()));
        nueva.setEstado(limpiar(maquina.estado()));
        maquinaRepository.save(nueva);
        return aDto(nueva);
    }

    @Override
    public synchronized MaquinaDTO actualizar(int id, MaquinaDTO cambios) {
        Maquina actual = buscar(id);
        if (cambios.id() != null && !cambios.id().equals(id)) {
            throw new DatosInvalidosException("No se permite modificar el identificador de la máquina.");
        }
        validar(cambios);
        actual.setDescripcion(limpiar(cambios.descripcion()));
        actual.setEstado(limpiar(cambios.estado()));
        maquinaRepository.save(actual);
        return aDto(actual);
    }

    @Override
    public synchronized void eliminar(int id) {
        Maquina maquina = buscar(id);
        maquinaRepository.delete(maquina);
    }

    private Maquina buscar(int id) {
        return maquinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("una máquina", id));
    }

    private static void validar(MaquinaDTO maquina) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "descripcion", maquina.descripcion());
        longitud(errores, "descripcion", maquina.descripcion(), 3, 50);
        longitud(errores, "estado", maquina.estado(), 0, 15);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);
    }

    private static MaquinaDTO aDto(Maquina m) {
        return new MaquinaDTO(m.getIdMaquina(), m.getDescripcion(), m.getEstado());
    }
}
