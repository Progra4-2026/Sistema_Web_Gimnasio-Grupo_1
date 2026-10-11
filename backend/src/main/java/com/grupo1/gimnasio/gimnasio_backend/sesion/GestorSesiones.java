package com.grupo1.gimnasio.gimnasio_backend.sesion;

import com.grupo1.gimnasio.gimnasio_backend.dto.UsuarioSesionDTO;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PATRÓN SINGLETON — GestorSesiones
 *
 * Registro único, compartido por toda la aplicación, de las sesiones HTTP activas
 * (id de sesión → usuario y hora de inicio). Debe existir una sola instancia: si hubiera
 * dos, un login quedaría registrado en una y el logout se buscaría en la otra.
 *
 * Implementación clásica del Singleton:
 *  1. Constructor privado: nadie puede hacer "new GestorSesiones()".
 *  2. Única instancia guardada en un campo static final.
 *  3. Punto de acceso global: GestorSesiones.getInstancia().
 *
 * Usado por AutenticacionServiceImpl. Además, todos los @Service del proyecto son beans
 * con alcance singleton (alcance por defecto de Spring), indicado con @Scope en cada uno.
 */
public final class GestorSesiones {

    /** Inicialización temprana (eager): segura entre hilos sin necesidad de synchronized. */
    private static final GestorSesiones INSTANCIA = new GestorSesiones();

    private final Map<String, SesionActiva> sesiones = new ConcurrentHashMap<>();

    private GestorSesiones() {
    }

    public static GestorSesiones getInstancia() {
        return INSTANCIA;
    }

    public void registrar(String idSesion, UsuarioSesionDTO usuario) {
        sesiones.put(idSesion, new SesionActiva(usuario, Instant.now()));
    }

    public void eliminar(String idSesion) {
        sesiones.remove(idSesion);
    }

    public Optional<SesionActiva> buscar(String idSesion) {
        return Optional.ofNullable(sesiones.get(idSesion));
    }

    public int cantidadActivas() {
        return sesiones.size();
    }

    /** Datos de una sesión activa. */
    public record SesionActiva(UsuarioSesionDTO usuario, Instant inicio) {
    }
}
