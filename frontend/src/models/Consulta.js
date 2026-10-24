/**
 * Modelo de una consulta del formulario de Contactos (POST /api/consultas).
 */
export default class Consulta {
  constructor({ nombre, email, telefono, asunto = 'general', mensaje }) {
    this.nombre = nombre;
    this.email = email;
    this.telefono = telefono;
    this.asunto = asunto;
    this.mensaje = mensaje;
  }

  /** Datos que se envían al backend. */
  aJson() {
    return { nombre: this.nombre, email: this.email, telefono: this.telefono, asunto: this.asunto, mensaje: this.mensaje };
  }
}
