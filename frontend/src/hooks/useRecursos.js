import { useCallback, useEffect, useReducer } from 'react';

const estadoInicial = { elementos: [], cargando: true, error: '' };

function reducer(estado, accion) {
  switch (accion.type) {
    case 'CARGA_INICIADA':
      return { ...estado, cargando: true, error: '' };
    case 'CARGA_EXITOSA':
      return { elementos: accion.payload, cargando: false, error: '' };
    case 'CARGA_FALLIDA':
      return { ...estado, cargando: false, error: accion.payload };
    default:
      return estado;
  }
}

/**
 * Hook reutilizable para las pantallas de mantenimiento: carga una lista desde la API
 * con useReducer (cargando / error / elementos) y expone recargar() para después de
 * crear, editar o eliminar.
 *
 *   const { elementos, cargando, error, recargar } = useRecursos(listarMaquinas);
 */
export default function useRecursos(cargarLista) {
  const [estado, dispatch] = useReducer(reducer, estadoInicial);

  const recargar = useCallback(async () => {
    dispatch({ type: 'CARGA_INICIADA' });
    try {
      dispatch({ type: 'CARGA_EXITOSA', payload: await cargarLista() });
    } catch (err) {
      dispatch({ type: 'CARGA_FALLIDA', payload: err.message });
    }
  }, [cargarLista]);

  useEffect(() => {
    recargar();
  }, [recargar]);

  return { ...estado, recargar };
}
