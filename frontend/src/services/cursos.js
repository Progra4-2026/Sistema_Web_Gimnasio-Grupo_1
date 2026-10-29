import { apiRequest } from './client';
import Curso from '../models/Curso';

/** GET /api/cursos → lista de Curso */
export async function listarCursos() {
  const datos = await apiRequest('/api/cursos');
  return datos.map(Curso.desdeJson);
}

/** POST /api/cursos → Curso creado (201) */
export async function crearCurso(datos) {
  const creado = await apiRequest('/api/cursos', { metodo: 'POST', cuerpo: new Curso(datos).aJson() });
  return Curso.desdeJson(creado);
}

/** PUT /api/cursos/{id} → { mensaje, registrosAfectados, dato: Curso } */
export async function actualizarCurso(id, datos) {
  const respuesta = await apiRequest(`/api/cursos/${id}`, { metodo: 'PUT', cuerpo: new Curso({ ...datos, id }).aJson() });
  return { ...respuesta, dato: Curso.desdeJson(respuesta.dato) };
}

/** DELETE /api/cursos/{id} → 204 sin cuerpo (409 si tiene clientes inscritos) */
export function eliminarCurso(id) {
  return apiRequest(`/api/cursos/${id}`, { metodo: 'DELETE' });
}
