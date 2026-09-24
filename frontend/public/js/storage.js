/* =========================================================
   Gimnasio PowerFit - Grupo 1
   storage.js — Almacenamiento en el navegador
   (Entregable 3, punto 5)

   localStorage  → persiste entre sesiones (aunque se cierre el navegador):
                   preferencias del usuario y datos de contacto recordados.
   sessionStorage → solo dura mientras la pestaña esté abierta:
                   borradores de formularios y caché de datos de la API.

   Todo acceso va en try/catch: el almacenamiento puede estar
   bloqueado (modo privado) o lleno, y la página debe seguir
   funcionando igual.
   NUNCA se guardan contraseñas.
   ========================================================= */

const PREFIJO = 'powerfit';

const CLAVES = Object.freeze({
    preferencias: `${PREFIJO}.preferencias`,
    datosUsuario: `${PREFIJO}.datosUsuario`,
    borrador: (idFormulario) => `${PREFIJO}.borrador.${idFormulario}`,
    cache: (nombre) => `${PREFIJO}.cache.${nombre}`
});

const CAMPOS_PROHIBIDOS = ['password', 'contrasena', 'contraseña'];

/* ---------- Funciones genéricas ---------- */

function leer(almacen, clave, porDefecto = null) {
    try {
        const texto = almacen.getItem(clave);
        return texto === null ? porDefecto : JSON.parse(texto);
    } catch (error) {
        console.warn(`[storage] No se pudo leer "${clave}":`, error);
        return porDefecto;
    }
}

function escribir(almacen, clave, valor) {
    try {
        almacen.setItem(clave, JSON.stringify(valor));
        return true;
    } catch (error) {
        // QuotaExceededError o almacenamiento deshabilitado
        console.warn(`[storage] No se pudo guardar "${clave}":`, error);
        return false;
    }
}

function eliminar(almacen, clave) {
    try {
        almacen.removeItem(clave);
    } catch (error) {
        console.warn(`[storage] No se pudo eliminar "${clave}":`, error);
    }
}

/** Quita cualquier campo sensible antes de guardar. */
function sinDatosSensibles(datos) {
    const limpio = {};
    for (const [clave, valor] of Object.entries(datos)) {
        if (!CAMPOS_PROHIBIDOS.includes(clave.toLowerCase())) limpio[clave] = valor;
    }
    return limpio;
}

/* =========================================================
   localStorage — datos persistentes entre sesiones
   ========================================================= */

const PREFERENCIAS_POR_DEFECTO = Object.freeze({
    recordarDatos: false,        // el usuario marcó "Recordar mis datos"
    asuntoConsulta: 'general',   // último asunto elegido en Contactos
    tipoInscripcion: ''          // último tipo de inscripción elegido
});

export function obtenerPreferencias() {
    return { ...PREFERENCIAS_POR_DEFECTO, ...leer(localStorage, CLAVES.preferencias, {}) };
}

/** Actualiza solo las preferencias indicadas y conserva las demás. */
export function guardarPreferencias(cambios) {
    return escribir(localStorage, CLAVES.preferencias, { ...obtenerPreferencias(), ...cambios });
}

/** Guarda nombre, correo y teléfono para autocompletar formularios en futuras visitas. */
export function guardarDatosUsuario({ nombre, email, telefono }) {
    return escribir(localStorage, CLAVES.datosUsuario, { nombre, email, telefono });
}

export function obtenerDatosUsuario() {
    return leer(localStorage, CLAVES.datosUsuario, null);
}

export function olvidarDatosUsuario() {
    eliminar(localStorage, CLAVES.datosUsuario);
}

/* =========================================================
   sessionStorage — datos temporales de la sesión
   ========================================================= */

/** Guarda lo que el usuario lleva escrito, para no perderlo si recarga la página. */
export function guardarBorrador(idFormulario, datos) {
    return escribir(sessionStorage, CLAVES.borrador(idFormulario), sinDatosSensibles(datos));
}

export function obtenerBorrador(idFormulario) {
    return leer(sessionStorage, CLAVES.borrador(idFormulario), null);
}

export function borrarBorrador(idFormulario) {
    eliminar(sessionStorage, CLAVES.borrador(idFormulario));
}

/** Caché de respuestas de la API durante la sesión (evita pedir lo mismo varias veces). */
export function guardarEnCache(nombre, datos) {
    return escribir(sessionStorage, CLAVES.cache(nombre), { guardadoEn: Date.now(), datos });
}

/**
 * @param {string} nombre
 * @param {number} vigenciaMs  tiempo máximo de validez del dato en caché
 */
export function obtenerDeCache(nombre, vigenciaMs = 5 * 60 * 1000) {
    const entrada = leer(sessionStorage, CLAVES.cache(nombre), null);
    if (!entrada || Date.now() - entrada.guardadoEn > vigenciaMs) return null;
    return entrada.datos;
}
