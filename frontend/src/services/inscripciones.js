import { apiRequest } from './client';
import Inscripcion from '../models/Inscripcion';

/** POST /api/inscripciones → { mensaje, inscripcion: Inscripcion } */
export async function crearInscripcion(datos) {
  const respuesta = await apiRequest('/api/inscripciones', { metodo: 'POST', cuerpo: datos });
  return { ...respuesta, inscripcion: Inscripcion.desdeJson(respuesta.inscripcion) };
}

/** DELETE /api/inscripciones/{cedula} */
export function eliminarInscripcion(cedula) {
  return apiRequest(`/api/inscripciones/${cedula}`, { metodo: 'DELETE' });
}
