/**
 * Modelo de una inscripción tal como la devuelve el backend (/api/inscripciones).
 * Nunca incluye la contraseña.
 */
export default class Inscripcion {
  constructor({ cedula, nombre, email, telefono, fechaNacimiento, edad, tipoInscripcion, idCurso, objetivo, fechaInscripcion }) {
    this.cedula = cedula;
    this.nombre = nombre;
    this.email = email;
    this.telefono = telefono;
    this.fechaNacimiento = fechaNacimiento;
    this.edad = edad;
    this.tipoInscripcion = tipoInscripcion;
    this.idCurso = idCurso;
    this.objetivo = objetivo;
    this.fechaInscripcion = fechaInscripcion;
  }

  static desdeJson(json) {
    return json ? new Inscripcion(json) : null;
  }

  esACurso() {
    return this.tipoInscripcion === 'curso';
  }
}
