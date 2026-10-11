package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import com.grupo1.gimnasio.gimnasio_backend.dto.ConsultaDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import com.grupo1.gimnasio.gimnasio_backend.services.ConsultaService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.grupo1.gimnasio.gimnasio_backend.services.impl.ReglasValidacion.*;

/**
 * Reglas de negocio de las consultas del formulario de Contactos.
 * Patrón Singleton: bean de alcance singleton. Datos en memoria hasta tener su repositorio.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConsultaServiceImpl implements ConsultaService {

    private static final Logger LOG = Logger.getLogger(ConsultaServiceImpl.class.getName());
    private static final Set<String> ASUNTOS = Set.of("general", "inscripcion", "cursos", "soporte");

    private final List<Map<String, Object>> consultas = new CopyOnWriteArrayList<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public List<Map<String, Object>> listar() {
        return List.copyOf(consultas);
    }

    @Override
    public Map<String, Object> crear(ConsultaDTO c) {
        Map<String, String> errores = new LinkedHashMap<>();
        requerido(errores, "nombre", c.nombre());
        longitud(errores, "nombre", c.nombre(), 3, 80);
        requerido(errores, "email", c.email());
        patron(errores, "email", c.email(), EMAIL, "Correo electrónico inválido.");
        requerido(errores, "telefono", c.telefono());
        patron(errores, "telefono", c.telefono(), TELEFONO, "El teléfono debe tener 8 dígitos.");
        requerido(errores, "mensaje", c.mensaje());
        longitud(errores, "mensaje", c.mensaje(), 10, 500);
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);

        String asunto = ASUNTOS.contains(c.asunto()) ? c.asunto() : "general";
        long id = secuencia.incrementAndGet();

        Map<String, Object> registro = new LinkedHashMap<>();
        registro.put("id", id);
        registro.put("nombre", limpiar(c.nombre()));
        registro.put("email", limpiar(c.email()));
        registro.put("telefono", limpiar(c.telefono()));
        registro.put("asunto", asunto);
        registro.put("mensaje", limpiar(c.mensaje()));
        registro.put("fecha", LocalDateTime.now().withNano(0).toString());
        consultas.add(registro);
        LOG.info(() -> "Consulta #" + id + " recibida (asunto: " + asunto + ")");

        return Map.of("id", id,
                "mensaje", "¡Gracias, " + limpiar(c.nombre()) + "! Recibimos tu consulta y te responderemos pronto.");
    }
}
