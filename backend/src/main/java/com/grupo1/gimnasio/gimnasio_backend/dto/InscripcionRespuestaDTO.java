package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Inscripción que devuelve la API. Nunca incluye la contraseña ni su hash.
 * edad se calcula a partir de la fecha de nacimiento. Los clientes registrados antes de la
 * inscripción web (datos de prueba de GIMNASIO.sql) no tienen fecha de nacimiento: edad es null.
 */
public record InscripcionRespuestaDTO(String cedula, String nombre, String email, String telefono,
                                      String fechaNacimiento, Integer edad, String tipoInscripcion,
                                      Integer idCurso, String objetivo, String fechaInscripcion) {
}
