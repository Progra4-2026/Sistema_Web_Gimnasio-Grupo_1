/* eslint-disable react-refresh/only-export-components */
import { createContext, useCallback, useContext, useEffect, useReducer } from 'react';
import { listarCursos } from '../services/cursos';

/**
 * Estado global de los cursos (useReducer + Context).
 * Cualquier componente puede leer la lista con useCursos() y pedir una recarga
 * con recargarCursos(), por ejemplo después de una inscripción o desinscripción,
 * para que los cupos que se muestran estén siempre actualizados.
 */
const CursosContext = createContext(null);

const estadoInicial = { cursos: [], cargando: true, error: '' };

function reducer(estado, accion) {
  switch (accion.type) {
    case 'CARGA_INICIADA':
      return { ...estado, cargando: true, error: '' };
    case 'CARGA_EXITOSA':
      return { cursos: accion.payload, cargando: false, error: '' };
    case 'CARGA_FALLIDA':
      return { ...estado, cargando: false, error: accion.payload };
    default:
      return estado;
  }
}

export function CursosProvider({ children }) {
  const [estado, dispatch] = useReducer(reducer, estadoInicial);

  const recargarCursos = useCallback(async () => {
    dispatch({ type: 'CARGA_INICIADA' });
    try {
      const cursos = await listarCursos();
      dispatch({ type: 'CARGA_EXITOSA', payload: cursos ?? [] });
    } catch (error) {
      dispatch({ type: 'CARGA_FALLIDA', payload: error.message });
    }
  }, []);

  useEffect(() => {
    recargarCursos();
  }, [recargarCursos]);

  return (
    <CursosContext.Provider value={{ ...estado, recargarCursos }}>
      {children}
    </CursosContext.Provider>
  );
}

export function useCursos() {
  const contexto = useContext(CursosContext);
  if (!contexto) throw new Error('useCursos debe usarse dentro de CursosProvider');
  return contexto;
}
