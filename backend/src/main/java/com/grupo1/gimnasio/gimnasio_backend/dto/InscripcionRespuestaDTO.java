package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Inscripción que devuelve la API. Nunca incluye la contraseña ni su hash.
 * edad se calcula a partir de la fecha de nacimiento (si se conoce).
 * tipoInscripcion/idCurso/objetivo solo vienen completos en la respuesta de "crear":
 * no tienen columna propia en la tabla cliente, así que listar()/obtener() no los recuerdan.
 */
public record InscripcionRespuestaDTO(String cedula, String nombre, String apellido1, String apellido2,
                                      String email, String telefono, String fechaNacimiento, Integer edad,
                                      String tipoInscripcion, Integer idCurso, String objetivo,
                                      String fechaInscripcion) {
}
