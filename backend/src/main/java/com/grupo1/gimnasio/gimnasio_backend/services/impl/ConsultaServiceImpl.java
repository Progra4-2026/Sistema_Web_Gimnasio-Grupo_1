package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.ConsultaDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.models.Consulta;
import com.grupo1.gimnasio.gimnasio_backend.repositories.ConsultaRepository;
import com.grupo1.gimnasio.gimnasio_backend.services.ConsultaService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Reglas de negocio de las consultas del formulario de Contactos (tabla consultas).
 * Patrón Singleton: bean de alcance singleton.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConsultaServiceImpl implements ConsultaService {

    private static final Logger LOG = Logger.getLogger(ConsultaServiceImpl.class.getName());
    private static final Set<String> ASUNTOS = Set.of("general", "inscripcion", "cursos", "soporte");

    private final ConsultaRepository consultaRepository;

    public ConsultaServiceImpl(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listar() {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Consulta c : consultaRepository.findAllByOrderByFechaDesc()) lista.add(aMapa(c));
        return lista;
    }

    @Override
    @Transactional
    public Map<String, Object> crear(ConsultaDTO c) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "nombre", c.nombre());
        longitud(errores, "nombre", c.nombre(), 3, 80);
        requerido(errores, "email", c.email());
        patron(errores, "email", c.email(), EMAIL, "Correo electrónico inválido.");
        longitud(errores, "email", c.email(), 5, 100);
        requerido(errores, "telefono", c.telefono());
        patron(errores, "telefono", c.telefono(), TELEFONO, "El teléfono debe tener 8 dígitos.");
        requerido(errores, "mensaje", c.mensaje());
        longitud(errores, "mensaje", c.mensaje(), 10, 500);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        Consulta consulta = new Consulta();
        consulta.setNombre(limpiar(c.nombre()));
        consulta.setEmail(limpiar(c.email()));
        consulta.setTelefono(limpiar(c.telefono()));
        consulta.setAsunto(ASUNTOS.contains(c.asunto()) ? c.asunto() : "general");
        consulta.setMensaje(limpiar(c.mensaje()));
        consulta.setFecha(LocalDateTime.now().withNano(0));
        Consulta guardada = consultaRepository.save(consulta);

        LOG.info(() -> "Consulta #" + guardada.getIdConsulta() + " recibida (asunto: " + guardada.getAsunto() + ")");
        return Map.of("id", guardada.getIdConsulta(),
                "mensaje", "¡Gracias, " + guardada.getNombre() + "! Recibimos tu consulta y te responderemos pronto.");
    }

    private static Map<String, Object> aMapa(Consulta c) {
        Map<String, Object> registro = new LinkedHashMap<>();
        registro.put("id", c.getIdConsulta());
        registro.put("nombre", c.getNombre());
        registro.put("email", c.getEmail());
        registro.put("telefono", c.getTelefono());
        registro.put("asunto", c.getAsunto());
        registro.put("mensaje", c.getMensaje());
        registro.put("fecha", c.getFecha().toString());
        return registro;
    }
}
