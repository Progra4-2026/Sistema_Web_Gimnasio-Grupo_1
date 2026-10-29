import { apiRequest } from './client';
import Instructor from '../models/Instructor';
import Administrador from '../models/Administrador';
import ParametroSistema from '../models/ParametroSistema';

// Servicios exclusivos del administrador (/api/admin/**). El backend responde 403 a cualquier otro rol.

/* ---------- Instructores ---------- */

export async function listarInstructores() {
  const datos = await apiRequest('/api/admin/instructores');
  return datos.map(Instructor.desdeJson);
}

export async function crearInstructor(datos, password) {
  const creado = await apiRequest('/api/admin/instructores', { metodo: 'POST', cuerpo: new Instructor(datos).aJson(password) });
  return Instructor.desdeJson(creado);
}

/** password vacía = conservar la contraseña actual */
export async function actualizarInstructor(codInstructor, datos, password) {
  const cuerpo = new Instructor({ ...datos, codInstructor }).aJson(password);
  const respuesta = await apiRequest(`/api/admin/instructores/${codInstructor}`, { metodo: 'PUT', cuerpo });
  return { ...respuesta, dato: Instructor.desdeJson(respuesta.dato) };
}

/** 409 si el instructor tiene rutinas o historial de cursos */
export function eliminarInstructor(codInstructor) {
  return apiRequest(`/api/admin/instructores/${codInstructor}`, { metodo: 'DELETE' });
}

/* ---------- Administradores ---------- */

export async function listarAdministradores() {
  const datos = await apiRequest('/api/admin/administradores');
  return datos.map(Administrador.desdeJson);
}

export async function crearAdministrador(datos, password) {
  const creado = await apiRequest('/api/admin/administradores', { metodo: 'POST', cuerpo: new Administrador(datos).aJson(password) });
  return Administrador.desdeJson(creado);
}

/** 409 si es el administrador con sesión iniciada o el último administrador */
export function eliminarAdministrador(codAdministrador) {
  return apiRequest(`/api/admin/administradores/${codAdministrador}`, { metodo: 'DELETE' });
}

/* ---------- Parámetros del sistema ---------- */

export async function listarParametros() {
  const datos = await apiRequest('/api/admin/parametros');
  return datos.map(ParametroSistema.desdeJson);
}

export async function actualizarParametro(clave, valor) {
  const cuerpo = new ParametroSistema({ clave, valor }).aJson();
  const respuesta = await apiRequest(`/api/admin/parametros/${encodeURIComponent(clave)}`, { metodo: 'PUT', cuerpo });
  return { ...respuesta, dato: ParametroSistema.desdeJson(respuesta.dato) };
}
