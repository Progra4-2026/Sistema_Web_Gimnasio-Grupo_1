import { apiRequest } from './client';

export function listarMaquinas() {
  return apiRequest('/api/maquinas');
}

export function crearMaquina(datos) {
  return apiRequest('/api/maquinas', { metodo: 'POST', cuerpo: datos });
}

export function actualizarMaquina(id, datos) {
  return apiRequest(`/api/maquinas/${id}`, { metodo: 'PUT', cuerpo: datos });
}

export function eliminarMaquina(id) {
  return apiRequest(`/api/maquinas/${id}`, { metodo: 'DELETE' });
}
