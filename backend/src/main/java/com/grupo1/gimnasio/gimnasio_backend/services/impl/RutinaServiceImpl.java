package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.RutinaDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.models.Cliente;
import com.grupo1.gimnasio.gimnasio_backend.models.Instructor;
import com.grupo1.gimnasio.gimnasio_backend.models.Maquina;
import com.grupo1.gimnasio.gimnasio_backend.models.Rutina;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ClienteRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.InstructorRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.MaquinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.repositories.RutinaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.RutinaService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Reglas de negocio de las rutinas (las gestiona el instructor; el cliente solo ve las suyas):
 *  - el cliente y la máquina deben existir; fecha válida y horas entre 1 y 24;
 *  - el instructor es siempre el de la sesión, nunca uno enviado por el navegador;
 *  - al actualizar solo cambian fecha y horas: cliente, máquina e instructor son llaves foráneas
 *    y no se modifican (regla del proyecto: no se actualizan llaves primarias ni foráneas).
 *
 * Patrón Singleton: bean de alcance singleton.
 * Patrón Iterator: los listados recorren las rutinas con java.util.Iterator.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RutinaServiceImpl implements RutinaService {

    private final RutinaRepository rutinaRepository;
    private final ClienteRepository clienteRepository;
    private final MaquinaRepository maquinaRepository;
    private final InstructorRepository instructorRepository;

    public RutinaServiceImpl(RutinaRepository rutinaRepository, ClienteRepository clienteRepository,
                             MaquinaRepository maquinaRepository, InstructorRepository instructorRepository) {
        this.rutinaRepository = rutinaRepository;
        this.clienteRepository = clienteRepository;
        this.maquinaRepository = maquinaRepository;
        this.instructorRepository = instructorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutinaDTO> listar() {
        return aLista(rutinaRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")).iterator());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutinaDTO> listarDeCliente(long cedula) {
        return aLista(rutinaRepository.findByCliente_CedulaOrderByFechaDesc(cedula).iterator());
    }

    @Override
    @Transactional(readOnly = true)
    public RutinaDTO obtener(long id) {
        return aDto(buscar(id));
    }

    @Override
    @Transactional
    public RutinaDTO crear(RutinaDTO datos, long codInstructor) {
        Map<String, String> errores = new LinkedHashMap<>();
        Cliente cliente = datos.cliente() == null ? null : clienteRepository.findById(datos.cliente()).orElse(null);
        if (cliente == null) errores.put("cliente", "Seleccioná un cliente válido.");
        Maquina maquina = datos.maquina() == null ? null : maquinaRepository.findById(datos.maquina()).orElse(null);
        if (maquina == null) errores.put("maquina", "Seleccioná una máquina válida.");
        LocalDate fecha = validarFecha(datos.fecha(), errores);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        Instructor instructor = instructorRepository.findById(codInstructor)
                .orElseThrow(() -> new RecursoNoEncontradoException("un instructor", codInstructor));

        Rutina nueva = new Rutina();
        nueva.setCliente(cliente);
        nueva.setMaquina(maquina);
        nueva.setInstructor(instructor);
        nueva.setFecha(fecha);
        nueva.setHoras(datos.horas());
        return aDto(rutinaRepository.save(nueva));
    }

    @Override
    @Transactional
    public RutinaDTO actualizar(long id, RutinaDTO cambios) {
        Rutina actual = buscar(id);
        if (cambios.id() != null && cambios.id() != id) {
            throw new DatosInvalidosException("No se permite modificar el identificador de la rutina.");
        }
        if (!Objects.equals(cambios.cliente(), actual.getCliente().getCedula())
                || !Objects.equals(cambios.maquina(), actual.getMaquina().getIdMaquina())
                || (cambios.instructor() != null
                    && !Objects.equals(cambios.instructor(), actual.getInstructor().getCodInstructor()))) {
            throw new DatosInvalidosException("No se permite cambiar el cliente, la máquina ni el instructor "
                    + "(llaves foráneas). Eliminá la rutina y creá una nueva.");
        }
        Map<String, String> errores = new LinkedHashMap<>();
        LocalDate fecha = validarFecha(cambios.fecha(), errores);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        actual.setFecha(fecha);
        actual.setHoras(cambios.horas());
        return aDto(rutinaRepository.save(actual));
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        rutinaRepository.delete(buscar(id));
    }

    /* ---------- auxiliares ---------- */

    private Rutina buscar(long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("una rutina", id));
    }

    private static LocalDate validarFecha(String texto, Map<String, String> errores) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            errores.put("fecha", "Usá el formato AAAA-MM-DD.");
            return null;
        }
    }

    /** PATRÓN ITERATOR: recorre la colección de rutinas con java.util.Iterator. */
    private static List<RutinaDTO> aLista(Iterator<Rutina> iterador) {
        List<RutinaDTO> lista = new ArrayList<>();
        while (iterador.hasNext()) {
            lista.add(aDto(iterador.next()));
        }
        return lista;
    }

    private static RutinaDTO aDto(Rutina r) {
        Cliente c = r.getCliente();
        String clienteNombre = String.join(" ", c.getNombre(), c.getApellido1(), c.getApellido2())
                .trim().replaceAll("\\s+", " ");
        return new RutinaDTO(r.getIdRutina(), c.getCedula(), r.getInstructor().getCodInstructor(),
                r.getMaquina().getIdMaquina(), r.getFecha().toString(), r.getHoras(),
                clienteNombre, r.getInstructor().nombreCompleto(), r.getMaquina().getDescripcion());
    }
}
