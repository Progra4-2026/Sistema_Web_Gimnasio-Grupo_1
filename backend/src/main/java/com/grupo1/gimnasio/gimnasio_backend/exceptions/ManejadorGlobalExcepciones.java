package com.grupo1.gimnasio.gimnasio_backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manejo central de excepciones para todos los controladores (@RestControllerAdvice).
 * Convierte cada excepción en una respuesta JSON con el código HTTP correcto:
 *   400 Bad Request   → DatosInvalidosException, validación de @Valid, JSON mal formado, parámetro inválido
 *   401 Unauthorized  → NoAutenticadoException, acceso a un servicio protegido sin sesión
 *   403 Forbidden     → @PreAuthorize rechazó el rol del usuario (por ejemplo, un cliente en /api/admin)
 *   404 Not Found     → RecursoNoEncontradoException, ruta inexistente
 *   405 Method Not Allowed → verbo HTTP no soportado en la ruta
 *   409 Conflict      → ConflictoException, violación de llave foránea o duplicado en la base de datos
 *   500 Internal Error→ cualquier otro error (se registra en la bitácora; al usuario solo un mensaje claro)
 * Cuerpo: { "mensaje": "...", "errores": { campo: mensaje } } — nunca trazas ni errores crudos de MySQL.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    private static final Logger LOG = Logger.getLogger(ManejadorGlobalExcepciones.class.getName());

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> manejarApiException(ApiException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", ex.getMessage());
        if (ex instanceof DatosInvalidosException invalidos && !invalidos.getErrores().isEmpty()) {
            cuerpo.put("errores", invalidos.getErrores());
        }
        return ResponseEntity.status(ex.getEstado()).body(cuerpo);
    }

    /** Errores de las anotaciones @NotBlank, @Email, @Size, @Pattern... en los DTOs marcados con @Valid. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", "Algunos datos no son válidos.");
        cuerpo.put("errores", errores);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", "El cuerpo de la solicitud no es un JSON válido."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", "El valor \"" + ex.getValue() + "\" no es válido para " + ex.getName() + "."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarRutaInexistente(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "La ruta solicitada no existe."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("mensaje", "La operación " + ex.getMethod() + " no está permitida en esta ruta."));
    }

    /** Restricción de la base de datos (llave foránea, duplicado, NOT NULL): sin mostrar el error crudo de MySQL. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(DataIntegrityViolationException ex) {
        LOG.log(Level.WARNING, "Violación de integridad en la base de datos", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", "La operación no se puede completar porque afecta datos relacionados."));
    }

    /**
     * @PreAuthorize rechazó el acceso dentro de un controlador. Va antes que el manejador genérico
     * de Exception: sin este método, un rol incorrecto terminaría como 500 en vez de 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> manejarAccesoDenegado(AccessDeniedException ex) {
        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        boolean sinSesion = actual == null || actual instanceof AnonymousAuthenticationToken;
        if (sinSesion) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Iniciá sesión para continuar."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tenés permiso para realizar esta acción."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorInesperado(Exception ex) {
        LOG.log(Level.SEVERE, "Error inesperado atendiendo la petición", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Ocurrió un error interno. Intentá de nuevo más tarde."));
    }
}
