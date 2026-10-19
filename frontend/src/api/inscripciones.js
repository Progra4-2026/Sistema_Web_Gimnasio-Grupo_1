import { apiRequest } from './client';

export function crearInscripcion(datos) {
  return apiRequest('/api/inscripciones', { metodo: 'POST', cuerpo: datos });
}

export function eliminarInscripcion(cedula) {
  return apiRequest(`/api/inscripciones/${cedula}`, { metodo: 'DELETE' });
}
