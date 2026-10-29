/**
 * Modelo del usuario con sesión iniciada (POST /api/auth/login y GET /api/auth/sesion).
 * "cedula" es el identificador con que inició sesión: cédula (cliente) o código (instructor/administrador).
 */
export const ROLES = Object.freeze({
  CLIENTE: 'cliente',
  INSTRUCTOR: 'instructor',
  ADMINISTRADOR: 'administrador',
});

// Página principal de cada rol después de iniciar sesión
const INICIO_POR_ROL = {
  [ROLES.CLIENTE]: '/servicios/rutinas',
  [ROLES.INSTRUCTOR]: '/instructor',
  [ROLES.ADMINISTRADOR]: '/mantenimiento',
};

// Secciones privadas y el rol que exige cada una
const SECCIONES_POR_ROL = [
  ['/mantenimiento', ROLES.ADMINISTRADOR],
  ['/instructor', ROLES.INSTRUCTOR],
];

export default class Usuario {
  constructor({ cedula, nombre, rol }) {
    this.cedula = cedula;
    this.nombre = nombre;
    this.rol = rol;
  }

  static desdeJson(json) {
    return json ? new Usuario(json) : null;
  }

  tieneRol(...roles) {
    return roles.includes(this.rol);
  }

  get esCliente() {
    return this.rol === ROLES.CLIENTE;
  }

  get esInstructor() {
    return this.rol === ROLES.INSTRUCTOR;
  }

  get esAdministrador() {
    return this.rol === ROLES.ADMINISTRADOR;
  }

  get rutaInicio() {
    return INICIO_POR_ROL[this.rol] || '/';
  }

  /** Indica si el rol del usuario puede abrir esa ruta del frontend. */
  puedeEntrar(ruta) {
    const seccion = SECCIONES_POR_ROL.find(([prefijo]) => ruta === prefijo || ruta.startsWith(`${prefijo}/`));
    return !seccion || this.rol === seccion[1];
  }

  /** Después del login: vuelve a la página pedida si el rol puede verla; si no, a la sección de su rol. */
  destinoTrasLogin(rutaPedida) {
    return rutaPedida && this.puedeEntrar(rutaPedida) ? rutaPedida : this.rutaInicio;
  }

  get rolLegible() {
    return { cliente: 'Cliente', instructor: 'Instructor', administrador: 'Administrador' }[this.rol] || this.rol;
  }
}
