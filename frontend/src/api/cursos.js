import { apiRequest } from './client';

export function listarCursos() {
  return apiRequest('/api/cursos');
}
