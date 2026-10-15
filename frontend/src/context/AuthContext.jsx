/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useEffect, useReducer } from 'react';
import { cerrarSesion as cerrarSesionApi, iniciarSesion as iniciarSesionApi, obtenerSesion } from '../api/auth';

const AuthContext = createContext(null);

const estadoInicial = { usuario: null, cargando: true };

function reducer(estado, accion) {
  switch (accion.type) {
    case 'CARGANDO':
      return { ...estado, cargando: true };
    case 'SESION_INICIADA':
      return { usuario: accion.payload, cargando: false };
    case 'SESION_CERRADA':
      return { usuario: null, cargando: false };
    default:
      return estado;
  }
}

export function AuthProvider({ children }) {
  const [estado, dispatch] = useReducer(reducer, estadoInicial);

  useEffect(() => {
    obtenerSesion()
      .then((datos) => dispatch({ type: 'SESION_INICIADA', payload: datos.usuario }))
      .catch(() => dispatch({ type: 'SESION_CERRADA' }));
  }, []);

  async function iniciarSesion(credenciales) {
    const datos = await iniciarSesionApi(credenciales);
    dispatch({ type: 'SESION_INICIADA', payload: datos.usuario });
    return datos;
  }

  async function cerrarSesion() {
    await cerrarSesionApi();
    dispatch({ type: 'SESION_CERRADA' });
  }

  return (
    <AuthContext.Provider value={{ ...estado, iniciarSesion, cerrarSesion }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const contexto = useContext(AuthContext);
  if (!contexto) throw new Error('useAuth debe usarse dentro de AuthProvider');
  return contexto;
}
