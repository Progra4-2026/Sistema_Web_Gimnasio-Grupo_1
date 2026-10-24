const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function mensajePorEstado(estado) {
  if (estado === 401) return 'Iniciá sesión para continuar.';
  if (estado === 404) return 'El recurso solicitado no existe.';
  if (estado >= 500) return 'Ocurrió un error en el servidor. Intentá de nuevo más tarde.';
  return 'No se pudo completar la solicitud.';
}

export async function apiRequest(ruta, { metodo = 'GET', cuerpo } = {}) {
  let respuesta;
  try {
    respuesta = await fetch(`${BASE_URL}${ruta}`, {
      method: metodo,
      headers: cuerpo ? { 'Content-Type': 'application/json', Accept: 'application/json' } : { Accept: 'application/json' },
      credentials: 'include',
      body: cuerpo ? JSON.stringify(cuerpo) : undefined,
    });
  } catch (errorRed) {
    // Servidor apagado, sin conexión o CORS: nunca se muestra el error crudo del navegador
    console.error(`[api] ${metodo} ${ruta}`, errorRed);
    const error = new Error('No se pudo conectar con el servidor. Verificá que el backend esté levantado.');
    error.estado = 0;
    throw error;
  }

  const texto = await respuesta.text();
  let datos = null;
  try {
    datos = texto ? JSON.parse(texto) : null;
  } catch {
    // Respuesta que no es JSON: datos queda en null y se usa el mensaje genérico de abajo
  }

  if (!respuesta.ok) {
    const error = new Error(datos?.mensaje || mensajePorEstado(respuesta.status));
    error.estado = respuesta.status;
    error.errores = datos?.errores;
    throw error;
  }

  return datos;
}
