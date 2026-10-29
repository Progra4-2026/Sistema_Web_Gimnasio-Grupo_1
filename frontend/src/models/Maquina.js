export const ESTADOS_MAQUINA = ['excelente', 'bueno', 'regular', 'en reparación'];

// Foto ilustrativa según el tipo de máquina (las imágenes están en public/assets)
const IMAGENES = [
  [/caminadora/i, 'caminadora_electrica.jpg'],
  [/bici|spinning/i, 'bici_estatica.jpg'],
  [/remo/i, 'maquina_remo.jpg'],
  [/el[ií]ptica/i, 'eliptica.jpg'],
  [/prensa|pierna|leg/i, 'leg_press.jpg'],
  [/multi|estaci[oó]n/i, 'estacion_multi_fuerza.jpg'],
  [/pesa|mancuerna|press|banca|barra/i, 'area_pesas.jpg'],
];

/** Modelo de una máquina, con la misma forma que devuelve GET /api/maquinas. */
export default class Maquina {
  constructor({ id = null, descripcion = '', estado = 'bueno' }) {
    this.id = id;
    this.descripcion = descripcion;
    this.estado = estado;
  }

  static desdeJson(json) {
    return new Maquina(json);
  }

  aJson() {
    return { id: this.id ?? null, descripcion: this.descripcion.trim(), estado: this.estado };
  }

  get disponible() {
    return this.estado !== 'en reparación';
  }

  get urlImagen() {
    const encontrada = IMAGENES.find(([patron]) => patron.test(this.descripcion));
    return `/assets/${encontrada ? encontrada[1] : 'zona-cardio.jpg'}`;
  }
}
