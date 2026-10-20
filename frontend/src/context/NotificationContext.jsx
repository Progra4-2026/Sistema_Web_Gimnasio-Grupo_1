/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useReducer } from 'react';

const NotificationContext = createContext(null);

const estadoInicial = { visible: false, tipo: 'info', texto: '' };

function reducer(estado, accion) {
  switch (accion.type) {
    case 'MOSTRAR':
      return { visible: true, tipo: accion.payload.tipo, texto: accion.payload.texto };
    case 'OCULTAR':
      return { ...estado, visible: false };
    default:
      return estado;
  }
}

export function NotificationProvider({ children }) {
  const [estado, dispatch] = useReducer(reducer, estadoInicial);

  function notificar(tipo, texto) {
    dispatch({ type: 'MOSTRAR', payload: { tipo, texto } });
  }

  function ocultar() {
    dispatch({ type: 'OCULTAR' });
  }

  return (
    <NotificationContext.Provider value={{ ...estado, notificar, ocultar }}>
      {children}
    </NotificationContext.Provider>
  );
}

export function useNotificacion() {
  const contexto = useContext(NotificationContext);
  if (!contexto) throw new Error('useNotificacion debe usarse dentro de NotificationProvider');
  return contexto;
}
