/* =========================================================
   Gimnasio PowerFit - Grupo 1
   api.js — Cliente HTTP hacia el backend Spring Boot
   (Entregable 3, punto 4)

   - fetch asíncrono con async/await (GET y POST).
   - Manejo de errores con try/catch: servidor caído, tiempo
     de espera agotado, respuestas 4xx/5xx y JSON inválido.
   - Nunca se muestran errores crudos al usuario: todo error se
     convierte en un ApiError con un mensaje entendible y el
     detalle técnico solo se registra en la consola (bitácora).
   ========================================================= */

export const API_BASE_URL = 'http://localhost:8080/api';
const TIEMPO_ESPERA_MS = 8000;

/** Error de la API con mensaje apto para mostrar al usuario. */
export class ApiError extends Error {
    constructor(mensaje, estado = 0, errores = null) {
        super(mensaje);
        this.name = 'ApiError';
        this.estado = estado;     // código HTTP (0 = sin respuesta del servidor)
        this.errores = errores;   // errores por campo que devuelve el backend, si hay
    }
}
//
const MENSAJES_POR_ESTADO = {
    400: 'Algunos datos no son válidos. Revisá el formulario.',
    404: 'El recurso solicitado no existe.',
    409: 'El registro ya existe.',
    500: 'Ocurrió un error en el servidor. Intentá de nuevo más tarde.'
};

function mensajePorEstado(estado) {
    return MENSAJES_POR_ESTADO[estado]
        ?? (estado >= 500 ? MENSAJES_POR_ESTADO[500] : 'No se pudo completar la solicitud.');
}

/** Lee el cuerpo JSON de la respuesta sin lanzar si viene vacío o mal formado. */
async function leerJson(respuesta) {
    if (respuesta.status === 204) return null;
    const texto = await respuesta.text();
    if (!texto) return null;
    try {
        return JSON.parse(texto);
    } catch (error) {
        console.error('[api] Respuesta no es JSON válido:', error, texto);
        return null;
    }
}

/**
 * Hace una petición al backend y devuelve el JSON de la respuesta.
 * @param {string} ruta   Ruta relativa, por ejemplo '/cursos'
 * @param {{metodo?: string, cuerpo?: object}} opciones
 * @returns {Promise<any>}
 * @throws {ApiError}
 */
async function peticion(ruta, { metodo = 'GET', cuerpo } = {}) {
    const controlador = new AbortController();
    const temporizador = setTimeout(() => controlador.abort(), TIEMPO_ESPERA_MS);

    let respuesta;
    try {
        respuesta = await fetch(`${API_BASE_URL}${ruta}`, {
            method: metodo,
            headers: {
                'Accept': 'application/json',
                ...(cuerpo !== undefined && { 'Content-Type': 'application/json' })
            },
            body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined,
            signal: controlador.signal
        });
    } catch (error) {
        console.error(`[api] ${metodo} ${ruta} falló:`, error);
        if (error.name === 'AbortError') {
            throw new ApiError('El servidor tardó demasiado en responder. Intentá de nuevo.');
        }
        throw new ApiError('No se pudo conectar con el servidor. Verificá que el backend esté levantado.');
    } finally {
        clearTimeout(temporizador);
    }

    const datos = await leerJson(respuesta);

    if (!respuesta.ok) {
        console.error(`[api] ${metodo} ${ruta} → ${respuesta.status}`, datos);
        throw new ApiError(datos?.mensaje || mensajePorEstado(respuesta.status),
            respuesta.status, datos?.errores ?? null);
    }

    return datos;
}

/* ---------- Endpoints del backend ---------- */

/** GET /api/health → { status: 'ok' } */
export function obtenerEstadoServidor() {
    return peticion('/health');
}

/** GET /api/cursos → lista de cursos */
export function obtenerCursos() {
    return peticion('/cursos');
}

/** POST /api/consultas → registra una consulta del formulario de Contactos */
export function enviarConsulta(consulta) {
    return peticion('/consultas', { metodo: 'POST', cuerpo: consulta });
}

/** POST /api/inscripciones → registra una inscripción (201, o 409 si la cédula ya existe) */
export function enviarInscripcion(inscripcion) {
    return peticion('/inscripciones', { metodo: 'POST', cuerpo: inscripcion });
}
