import { apiRequest } from './client';

export function listarRutinas() {
  return apiRequest('/api/rutinas');
}

export function misRutinas() {
  return apiRequest('/api/rutinas/mias');
}

export function crearRutina(datos) {
  return apiRequest('/api/rutinas', { metodo: 'POST', cuerpo: datos });
}

export function actualizarRutina(id, datos) {
  return apiRequest(`/api/rutinas/${id}`, { metodo: 'PUT', cuerpo: datos });
}

export function eliminarRutina(id) {
  return apiRequest(`/api/rutinas/${id}`, { metodo: 'DELETE' });
}
