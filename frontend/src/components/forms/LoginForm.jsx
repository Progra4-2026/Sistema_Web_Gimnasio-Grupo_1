import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

/**
 * Inicio de sesión de los tres roles con el mismo formulario:
 * cliente → cédula, instructor → código de instructor, administrador → código de administrador.
 * onExito recibe el usuario para que la página decida a dónde redirigir.
 */
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
    if (!/^\d{1,12}$/.test(datos.cedula.trim())) {
      setError('El usuario es tu cédula o tu código: solo números.');
      return;
    }
    setEnviando(true);
    try {
      const respuesta = await iniciarSesion({ cedula: datos.cedula.trim(), password: datos.password });
      notificar('exito', respuesta.mensaje);
      setDatos({ cedula: '', password: '' });
      onExito?.(respuesta.usuario);
    } catch (err) {
      setError(err.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form className="formulario formulario-inline" onSubmit={manejarEnvio}>
      <div className="campo-formulario">
        <label htmlFor="login-usuario">Usuario</label>
        <input id="login-usuario" name="cedula" value={datos.cedula} onChange={manejarCambio} placeholder="Cédula o código" inputMode="numeric" autoComplete="username" required />
      </div>
      <div className="campo-formulario">
        <label htmlFor="login-password">Contraseña</label>
        <input type="password" id="login-password" name="password" value={datos.password} onChange={manejarCambio} placeholder="Contraseña" autoComplete="current-password" required />
      </div>
      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Ingresando...' : 'Iniciar sesión'}
      </button>
      {error && <p className="texto-error">{error}</p>}
    </form>
  );
}
