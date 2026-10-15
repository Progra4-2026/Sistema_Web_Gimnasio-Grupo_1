import { apiRequest } from './client';

export function iniciarSesion(credenciales) {
  return apiRequest('/api/auth/login', { metodo: 'POST', cuerpo: credenciales });
}

export function obtenerSesion() {
  return apiRequest('/api/auth/sesion');
}

export function cerrarSesion() {
  return apiRequest('/api/auth/logout', { metodo: 'POST' });
}
