/** Modelo de un parámetro del sistema (GET /api/admin/parametros). La clave es la llave primaria. */
export default class ParametroSistema {
  constructor({ clave, valor = '', descripcion = '' }) {
    this.clave = clave;
    this.valor = valor;
    this.descripcion = descripcion ?? '';
  }

  static desdeJson(json) {
    return new ParametroSistema(json);
  }

  aJson() {
    return { clave: this.clave, valor: String(this.valor).trim(), descripcion: this.descripcion };
  }
}
