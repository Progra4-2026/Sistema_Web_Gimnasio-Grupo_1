import { useState } from 'react';
import { crearAdministrador } from '../../api/admin';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

const VALOR_INICIAL = { codAdministrador: '', nombre: '', email: '', password: '' };

export default function AdministradorForm({ onGuardado }) {
  const [datos, setDatos] = useState(VALOR_INICIAL);
  const [errores, setErrores] = useState({});
  const [enviando, setEnviando] = useState(false);
  const { notificar } = useNotificacion();

  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setDatos((anterior) => ({ ...anterior, [name]: value }));
  }

  async function manejarEnvio(evento) {
    evento.preventDefault();
    setEnviando(true);
    try {
      await crearAdministrador({ ...datos, codAdministrador: Number(datos.codAdministrador) });
      notificar('exito', 'Administrador creado correctamente.');
      setDatos(VALOR_INICIAL);
      onGuardado?.();
    } catch (error) {
      setErrores(error.errores || {});
      notificar('error', error.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      <div className="campo-formulario">
        <label htmlFor="admin-cod">Código <span className="obligatorio">*</span></label>
        <input id="admin-cod" name="codAdministrador" value={datos.codAdministrador} onChange={manejarCambio} required />
        {errores.codAdministrador && <p className="texto-error">{errores.codAdministrador}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="admin-nombre">Nombre <span className="obligatorio">*</span></label>
        <input id="admin-nombre" name="nombre" value={datos.nombre} onChange={manejarCambio} required />
        {errores.nombre && <p className="texto-error">{errores.nombre}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="admin-email">Correo electrónico</label>
        <input id="admin-email" name="email" value={datos.email} onChange={manejarCambio} />
        {errores.email && <p className="texto-error">{errores.email}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="admin-password">Contraseña</label>
        <input type="password" id="admin-password" name="password" value={datos.password} onChange={manejarCambio}
               placeholder="Por defecto: el código" />
        <span className="texto-ayuda">Se almacenará cifrada. Si se deja vacío, queda el código como contraseña.</span>
      </div>
      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Guardando...' : 'Crear administrador'}
      </button>
    </form>
  );
}
