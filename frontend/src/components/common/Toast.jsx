import { useEffect } from 'react';
import { useNotificacion } from '../../context/NotificationContext';
import './Toast.css';

export default function Toast() {
  const { visible, tipo, texto, ocultar } = useNotificacion();

  useEffect(() => {
    if (!visible) return;
    const temporizador = setTimeout(ocultar, 4000);
    return () => clearTimeout(temporizador);
  }, [visible, texto, ocultar]);

  if (!visible) return null;

  return (
    <div className={`toast toast--${tipo}`} role="status" aria-live="polite">
      <span>{texto}</span>
      <button type="button" aria-label="Cerrar notificación" onClick={ocultar}>
        ×
      </button>
    </div>
  );
}
