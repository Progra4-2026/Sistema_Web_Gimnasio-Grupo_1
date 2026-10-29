import { apiRequest } from './client';
import Rutina from '../models/Rutina';

/** GET /api/rutinas/mias → rutinas del cliente con sesión iniciada (solo cliente) */
export async function listarMisRutinas() {
  const datos = await apiRequest('/api/rutinas/mias');
  return datos.map(Rutina.desdeJson);
}

/** GET /api/rutinas → todas las rutinas (solo instructor) */
export async function listarRutinas() {
  const datos = await apiRequest('/api/rutinas');
  return datos.map(Rutina.desdeJson);
}

/** POST /api/rutinas → Rutina creada (201); el instructor es el de la sesión */
export async function crearRutina(datos) {
  const creada = await apiRequest('/api/rutinas', { metodo: 'POST', cuerpo: new Rutina(datos).aJson() });
  return Rutina.desdeJson(creada);
}

/** PUT /api/rutinas/{id} → solo cambian fecha y horas (cliente y máquina son llaves foráneas) */
export async function actualizarRutina(id, datos) {
  const respuesta = await apiRequest(`/api/rutinas/${id}`, { metodo: 'PUT', cuerpo: new Rutina({ ...datos, id }).aJson() });
  return { ...respuesta, dato: Rutina.desdeJson(respuesta.dato) };
}

/** DELETE /api/rutinas/{id} → 204 */
export function eliminarRutina(id) {
  return apiRequest(`/api/rutinas/${id}`, { metodo: 'DELETE' });
}
