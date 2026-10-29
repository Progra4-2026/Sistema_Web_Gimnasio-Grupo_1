/** Modelo de un administrador (GET /api/admin/administradores). */
export default class Administrador {
  constructor({ codAdministrador = null, nombre = '', email = '' }) {
    this.codAdministrador = codAdministrador;
    this.nombre = nombre;
    this.email = email ?? '';
  }

  static desdeJson(json) {
    return new Administrador(json);
  }

  aJson(password) {
    return { nombre: this.nombre.trim(), email: this.email.trim() || null, password };
  }
}
