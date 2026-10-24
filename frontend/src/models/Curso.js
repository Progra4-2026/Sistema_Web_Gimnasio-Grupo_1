/**
 * Modelo de un curso, con la misma forma que devuelve GET /api/cursos.
 */
export default class Curso {
  constructor({ id, descripcion, detalle = '', horario, cupos, imagen }) {
    this.id = id;
    this.descripcion = descripcion;
    this.detalle = detalle;
    this.horario = horario;
    this.cupos = Number(cupos);
    this.imagen = imagen;
  }

  /** Convierte el JSON del backend en un Curso. */
  static desdeJson(json) {
    return new Curso(json);
  }

  /** Datos que se envían al backend en POST/PUT. */
  aJson() {
    return {
      id: this.id ?? null,
      descripcion: this.descripcion,
      detalle: this.detalle,
      horario: this.horario,
      cupos: this.cupos,
      imagen: this.imagen || null,
    };
  }

  tieneCupos() {
    return this.cupos > 0;
  }

  /** El backend guarda solo el nombre del archivo; las imágenes están en public/assets. */
  get urlImagen() {
    return this.imagen ? `/assets/${this.imagen}` : null;
  }
}
