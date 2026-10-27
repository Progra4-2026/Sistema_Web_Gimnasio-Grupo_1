package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.RutinaDTO;
import com.grupo1.gimnasio.gimnasio_backend.entities.Rutina;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.MaquinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.RutinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.RutinaService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.requerido;
import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.vacio;

/**
 * Reglas de negocio de las rutinas (gestionadas por el instructor, el cliente solo las ve):
 *  - cliente y máquina deben existir;
 *  - fecha obligatoria y horas entre 1 y 24;
 *  - el instructor nunca viene del cuerpo de la petición: lo fija el controlador con el
 *    código del instructor autenticado (nunca se confía en un id enviado por el cliente).
 *
 * Patrón Singleton: bean de alcance singleton. Persistencia real en MySQL vía RutinaRepository.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RutinaServiceImpl implements RutinaService {

    private final RutinaRepository rutinaRepository;
    private final ClienteRepository clienteRepository;
    private final MaquinaRepository maquinaRepository;

    public RutinaServiceImpl(RutinaRepository rutinaRepository, ClienteRepository clienteRepository,
                              MaquinaRepository maquinaRepository) {
        this.rutinaRepository = rutinaRepository;
        this.clienteRepository = clienteRepository;
        this.maquinaRepository = maquinaRepository;
    }

    @Override
    public List<RutinaDTO> listar() {
        return rutinaRepository.findAll().stream()
                .sorted(Comparator.comparing(Rutina::getIdRutina))
                .map(RutinaServiceImpl::aDto)
                .toList();
    }

    @Override
    public List<RutinaDTO> listarDeCliente(int cedulaCliente) {
        return rutinaRepository.findByCliente(cedulaCliente).stream()
                .sorted(Comparator.comparing(Rutina::getIdRutina))
                .map(RutinaServiceImpl::aDto)
                .toList();
    }

    @Override
    public RutinaDTO obtener(int id) {
        return aDto(buscar(id));
    }

    @Override
    public synchronized RutinaDTO crear(RutinaDTO rutina, int codInstructor) {
        LocalDate fecha = validar(rutina);

        Rutina nueva = new Rutina();
        int id = rutinaRepository.findAll().stream().mapToInt(Rutina::getIdRutina).max().orElse(0) + 1;
        nueva.setIdRutina(id);
        nueva.setCliente(rutina.cliente());
        nueva.setInstructor(codInstructor);
        nueva.setMaquina(rutina.maquina());
        nueva.setFecha(fecha);
        nueva.setHoras(rutina.horas());
        rutinaRepository.save(nueva);
        return aDto(nueva);
    }

    @Override
    public synchronized RutinaDTO actualizar(int id, RutinaDTO cambios) {
        Rutina actual = buscar(id);
        if (cambios.id() != null && !cambios.id().equals(id)) {
            throw new DatosInvalidosException("No se permite modificar el identificador de la rutina.");
        }
        LocalDate fecha = validar(cambios);
        actual.setCliente(cambios.cliente());
        actual.setMaquina(cambios.maquina());
        actual.setFecha(fecha);
        actual.setHoras(cambios.horas());
        rutinaRepository.save(actual);
        return aDto(actual);
    }

    @Override
    public synchronized void eliminar(int id) {
        rutinaRepository.delete(buscar(id));
    }

    private Rutina buscar(int id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("una rutina", id));
    }

    private LocalDate validar(RutinaDTO rutina) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (rutina.cliente() == null || !clienteRepository.existsById(rutina.cliente())) {
            errores.put("cliente", "Seleccioná un cliente válido.");
        }
        if (rutina.maquina() == null || !maquinaRepository.existsById(rutina.maquina())) {
            errores.put("maquina", "Seleccioná una máquina válida.");
        }
        if (rutina.horas() == null || rutina.horas() < 1 || rutina.horas() > 24) {
            errores.put("horas", "Las horas deben estar entre 1 y 24.");
        }
        requerido(errores, "fecha", rutina.fecha());
        LocalDate fecha = null;
        if (!vacio(rutina.fecha())) {
            try {
                fecha = LocalDate.parse(rutina.fecha().trim());
            } catch (DateTimeParseException e) {
                errores.put("fecha", "Usá el formato AAAA-MM-DD.");
            }
        }
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);
        return fecha;
    }

    private static RutinaDTO aDto(Rutina r) {
        return new RutinaDTO(r.getIdRutina(), r.getCliente(), r.getInstructor(), r.getMaquina(),
                r.getFecha().toString(), r.getHoras());
    }
}
