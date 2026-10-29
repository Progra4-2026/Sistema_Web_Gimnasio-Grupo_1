/** Modelo de un instructor (GET /api/admin/instructores). La contraseña nunca viene del backend. */
export default class Instructor {
  constructor({ codInstructor = null, nombre = '', apellido1 = '', apellido2 = '', direccion = '', email = '', telCel = '', fechaContratacion = '' }) {
    this.codInstructor = codInstructor;
    this.nombre = nombre;
    this.apellido1 = apellido1;
    this.apellido2 = apellido2;
    this.direccion = direccion ?? '';
    this.email = email ?? '';
    this.telCel = telCel;
    this.fechaContratacion = fechaContratacion;
  }

  static desdeJson(json) {
    return new Instructor(json);
  }

  /** password: obligatoria al crear; al editar, vacía = conservar la actual. */
  aJson(password) {
    return {
      codInstructor: this.codInstructor ?? null,
      nombre: this.nombre.trim(),
      apellido1: this.apellido1.trim(),
      apellido2: this.apellido2.trim(),
      direccion: this.direccion.trim() || null,
      email: this.email.trim() || null,
      telCel: Number(this.telCel),
      fechaContratacion: this.fechaContratacion,
      password: password ? password : null,
    };
  }

  get nombreCompleto() {
    return [this.nombre, this.apellido1, this.apellido2].filter(Boolean).join(' ');
  }
}
