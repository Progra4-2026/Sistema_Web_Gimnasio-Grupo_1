/**
 * Modelo del usuario con sesión iniciada (POST /api/auth/login y GET /api/auth/sesion).
 */
export default class Usuario {
  constructor({ cedula, nombre, rol }) {
    this.cedula = cedula;
    this.nombre = nombre;
    this.rol = rol;
  }

  static desdeJson(json) {
    return json ? new Usuario(json) : null;
  }

  tieneRol(rol) {
    return this.rol === rol;
  }
}
