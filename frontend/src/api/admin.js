import { apiRequest } from './client';

// Clientes: reusa el CRUD de /api/inscripciones, ahora protegido para ADMINISTRADOR.
export function listarClientes() {
  return apiRequest('/api/inscripciones');
}

export function eliminarCliente(cedula) {
  return apiRequest(`/api/inscripciones/${cedula}`, { metodo: 'DELETE' });
}

export function listarInstructores() {
  return apiRequest('/api/admin/instructores');
}

export function crearInstructor(datos) {
  return apiRequest('/api/admin/instructores', { metodo: 'POST', cuerpo: datos });
}

export function actualizarInstructor(codInstructor, datos) {
  return apiRequest(`/api/admin/instructores/${codInstructor}`, { metodo: 'PUT', cuerpo: datos });
}

export function eliminarInstructor(codInstructor) {
  return apiRequest(`/api/admin/instructores/${codInstructor}`, { metodo: 'DELETE' });
}

export function listarAdministradores() {
  return apiRequest('/api/admin/administradores');
}

export function crearAdministrador(datos) {
  return apiRequest('/api/admin/administradores', { metodo: 'POST', cuerpo: datos });
}

export function eliminarAdministrador(codAdministrador) {
  return apiRequest(`/api/admin/administradores/${codAdministrador}`, { metodo: 'DELETE' });
}

export function listarParametros() {
  return apiRequest('/api/admin/parametros');
}

export function actualizarParametro(clave, datos) {
  return apiRequest(`/api/admin/parametros/${clave}`, { metodo: 'PUT', cuerpo: datos });
}
