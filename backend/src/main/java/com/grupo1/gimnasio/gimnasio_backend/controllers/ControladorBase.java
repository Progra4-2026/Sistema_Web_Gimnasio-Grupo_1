package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.exceptions.ApiException;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.DatosInvalidosException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convierte las excepciones en respuestas JSON con el código HTTP correcto:
 *   400 Bad Request   → DatosInvalidosException, JSON mal formado, parámetro con formato inválido
 *   401 Unauthorized  → NoAutenticadoException
 *   404 Not Found     → RecursoNoEncontradoException
 *   409 Conflict      → ConflictoException
 *   500 Internal Error→ cualquier otro error (se registra en la bitácora; al usuario solo un mensaje claro)
 * Cuerpo: { "mensaje": "...", "errores": { campo: mensaje } }  — nunca trazas ni errores crudos.
 *
 * NOTA: cuando se agregue el @ControllerAdvice central (punto "Manejo central de excepciones"),
 * estos métodos se pueden mover allí tal cual y quitar el "extends ControladorBase".
 */
public abstract class ControladorBase {

    private static final Logger LOG = Logger.getLogger(ControladorBase.class.getName());

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> manejarApiException(ApiException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", ex.getMessage());
        if (ex instanceof DatosInvalidosException invalidos && !invalidos.getErrores().isEmpty()) {
            cuerpo.put("errores", invalidos.getErrores());
        }
        return ResponseEntity.status(ex.getEstado()).body(cuerpo);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorInesperado(Exception ex) {
        LOG.log(Level.SEVERE, "Error inesperado atendiendo la petición", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Ocurrió un error interno. Intentá de nuevo más tarde."));
    }
}
