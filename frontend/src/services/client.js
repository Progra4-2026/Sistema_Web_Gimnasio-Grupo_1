const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export async function apiRequest(ruta, { metodo = 'GET', cuerpo } = {}) {
  const respuesta = await fetch(`${BASE_URL}${ruta}`, {
    method: metodo,
    headers: cuerpo ? { 'Content-Type': 'application/json', Accept: 'application/json' } : { Accept: 'application/json' },
    credentials: 'include',
    body: cuerpo ? JSON.stringify(cuerpo) : undefined,
  });

  const texto = await respuesta.text();
  const datos = texto ? JSON.parse(texto) : null;

  if (!respuesta.ok) {
    const error = new Error(datos?.mensaje || `Error ${respuesta.status}`);
    error.estado = respuesta.status;
    error.errores = datos?.errores;
    throw error;
  }

  return datos;
}
