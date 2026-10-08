package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Inscripción que devuelve la API. Nunca incluye la contraseña ni su hash.
 * edad se calcula a partir de la fecha de nacimiento.
 */
public record InscripcionRespuestaDTO(String cedula, String nombre, String email, String telefono,
                                      String fechaNacimiento, int edad, String tipoInscripcion,
                                      Integer idCurso, String objetivo, String fechaInscripcion) {
}
