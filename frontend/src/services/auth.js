import { apiRequest } from './client';
import Usuario from '../models/Usuario';

/** POST /api/auth/login → { mensaje, usuario: Usuario } */
export async function iniciarSesion(credenciales) {
  const datos = await apiRequest('/api/auth/login', { metodo: 'POST', cuerpo: credenciales });
  return { ...datos, usuario: Usuario.desdeJson(datos.usuario) };
}

/** GET /api/auth/sesion → { usuario: Usuario } (401 si no hay sesión) */
export async function obtenerSesion() {
  const datos = await apiRequest('/api/auth/sesion');
  return { ...datos, usuario: Usuario.desdeJson(datos.usuario) };
}

/** POST /api/auth/logout */
export function cerrarSesion() {
  return apiRequest('/api/auth/logout', { metodo: 'POST' });
}
