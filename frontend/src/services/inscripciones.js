import { apiRequest } from './client';
import Inscripcion from '../models/Inscripcion';

/** GET /api/inscripciones → lista de Inscripcion (sin contraseñas) */
export async function listarInscripciones() {
  const datos = await apiRequest('/api/inscripciones');
  return datos.map(Inscripcion.desdeJson);
}

/** POST /api/inscripciones → { mensaje, inscripcion: Inscripcion } */
export async function crearInscripcion(datos) {
  const respuesta = await apiRequest('/api/inscripciones', { metodo: 'POST', cuerpo: datos });
  return { ...respuesta, inscripcion: Inscripcion.desdeJson(respuesta.inscripcion) };
}

/**
 * PUT /api/inscripciones/{cedula} → { mensaje, registrosAfectados, dato: Inscripcion }
 * Solo datos de contacto: el backend no permite cambiar la cédula (PK) ni el curso (FK).
 */
export async function actualizarInscripcion(cedula, { nombre, email, telefono, fechaNacimiento, objetivo }) {
  const cuerpo = { nombre, email, telefono, fechaNacimiento, ...(objetivo ? { objetivo } : {}) };
  const respuesta = await apiRequest(`/api/inscripciones/${cedula}`, { metodo: 'PUT', cuerpo });
  return { ...respuesta, dato: Inscripcion.desdeJson(respuesta.dato) };
}

/** DELETE /api/inscripciones/{cedula} */
export function eliminarInscripcion(cedula) {
  return apiRequest(`/api/inscripciones/${cedula}`, { metodo: 'DELETE' });
}
