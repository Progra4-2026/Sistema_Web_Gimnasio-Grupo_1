import { apiRequest } from './client';

export function crearConsulta(datos) {
  return apiRequest('/api/consultas', { metodo: 'POST', cuerpo: datos });
}
