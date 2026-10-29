import { apiRequest } from './client';
import Maquina from '../models/Maquina';

/** GET /api/maquinas → lista de Maquina (público) */
export async function listarMaquinas() {
  const datos = await apiRequest('/api/maquinas');
  return datos.map(Maquina.desdeJson);
}

/** POST /api/maquinas → Maquina creada (201). Solo instructor. */
export async function crearMaquina(datos) {
  const creada = await apiRequest('/api/maquinas', { metodo: 'POST', cuerpo: new Maquina(datos).aJson() });
  return Maquina.desdeJson(creada);
}

/** PUT /api/maquinas/{id} → { mensaje, registrosAfectados, dato: Maquina }. Solo instructor. */
export async function actualizarMaquina(id, datos) {
  const respuesta = await apiRequest(`/api/maquinas/${id}`, { metodo: 'PUT', cuerpo: new Maquina({ ...datos, id }).aJson() });
  return { ...respuesta, dato: Maquina.desdeJson(respuesta.dato) };
}

/** DELETE /api/maquinas/{id} → 204 (409 si la máquina está en alguna rutina). Solo instructor. */
export function eliminarMaquina(id) {
  return apiRequest(`/api/maquinas/${id}`, { metodo: 'DELETE' });
}
