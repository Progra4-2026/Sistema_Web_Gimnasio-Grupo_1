/**
 * Modelo de una rutina (GET /api/rutinas y /api/rutinas/mias).
 * cliente, instructor y maquina son las llaves foráneas; los *Nombre/*Descripcion vienen listos para mostrar.
 */
export default class Rutina {
  constructor({ id = null, cliente, instructor = null, maquina, fecha, horas, clienteNombre = '', instructorNombre = '', maquinaDescripcion = '' }) {
    this.id = id;
    this.cliente = cliente;
    this.instructor = instructor;
    this.maquina = maquina;
    this.fecha = fecha;
    this.horas = Number(horas);
    this.clienteNombre = clienteNombre;
    this.instructorNombre = instructorNombre;
    this.maquinaDescripcion = maquinaDescripcion;
  }

  static desdeJson(json) {
    return new Rutina(json);
  }

  /** El instructor no se envía: el backend usa el instructor con sesión iniciada. */
  aJson() {
    return {
      id: this.id ?? null,
      cliente: Number(this.cliente),
      maquina: Number(this.maquina),
      fecha: this.fecha,
      horas: Number(this.horas),
    };
  }

  /** Fecha en formato local (dd/mm/aaaa) sin desfase de zona horaria. */
  get fechaLegible() {
    const [anio, mes, dia] = String(this.fecha).split('-');
    return dia ? `${dia}/${mes}/${anio}` : this.fecha;
  }
}
