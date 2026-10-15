import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

export default function LoginForm({ onExito }) {
  const [datos, setDatos] = useState({ cedula: '', password: '' });
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);
  const { iniciarSesion } = useAuth();
  const { notificar } = useNotificacion();

  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setDatos((anterior) => ({ ...anterior, [name]: value }));
  }

  async function manejarEnvio(evento) {
    evento.preventDefault();
    setError('');
    setEnviando(true);
    try {
      const respuesta = await iniciarSesion(datos);
      notificar('exito', respuesta.mensaje);
      setDatos({ cedula: '', password: '' });
      onExito?.();
    } catch (err) {
      setError(err.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form className="formulario formulario-inline" onSubmit={manejarEnvio}>
      <div className="campo-formulario">
        <label htmlFor="rut-cedula">Cédula</label>
        <input id="rut-cedula" name="cedula" value={datos.cedula} onChange={manejarCambio} placeholder="Cédula" required />
      </div>
      <div className="campo-formulario">
        <label htmlFor="rut-password">Contraseña</label>
        <input type="password" id="rut-password" name="password" value={datos.password} onChange={manejarCambio} placeholder="Contraseña" required />
      </div>
      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Ingresando...' : 'Iniciar sesión'}
      </button>
      {error && <p className="texto-error">{error}</p>}
    </form>
  );
}
