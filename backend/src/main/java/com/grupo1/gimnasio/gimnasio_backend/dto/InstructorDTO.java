package com.grupo1.gimnasio.gimnasio_backend.dto;

/**
 * Instructor tal como lo envía y recibe la API (gestionado por el administrador).
 * password es opcional: si se omite al crear, queda la cédula/código como contraseña
 * por defecto (igual que CargaInicialDatosRunner); nunca se devuelve en la respuesta.
 */
public record InstructorDTO(Integer codInstructor, String nombre, String apellido1, String apellido2,
                            String direccion, String email, Integer telCel, Integer telHabitacion,
                            String fechaContratacion, String password) {
}
