import { apiRequest } from './client';
import Curso from '../models/Curso';

/** GET /api/cursos → lista de Curso */
export async function listarCursos() {
  const datos = await apiRequest('/api/cursos');
  return datos.map(Curso.desdeJson);
}
