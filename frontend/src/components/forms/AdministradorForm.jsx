import { useState } from 'react';
import './Forms.css';

function validar(datos) {
  const errores = {};
  const nombre = datos.nombre.trim();
  if (nombre.length < 2 || nombre.length > 60) errores.nombre = 'Entre 2 y 60 caracteres.';
  const email = datos.email.trim();
  if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) errores.email = 'Correo electrónico no válido.';
  if (datos.password.length < 8) errores.password = 'Mínimo 8 caracteres.';
  else if (datos.password !== datos.confirmacion) errores.confirmacion = 'Las contraseñas no coinciden.';
  return errores;
}

/** Crear un administrador. El código (su usuario) lo asigna la base de datos. */
export default function AdministradorForm({ onGuardar, onCancelar }) {
  const [datos, setDatos] = useState({ nombre: '', email: '', password: '', confirmacion: '' });
  const [errores, setErrores] = useState({});
  const [guardando, setGuardando] = useState(false);

  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setDatos((anterior) => ({ ...anterior, [name]: value }));
  }

  async function manejarEnvio(evento) {
    evento.preventDefault();
    const encontrados = validar(datos);
    setErrores(encontrados);
    if (Object.keys(encontrados).length > 0) return;

    setGuardando(true);
    try {
      await onGuardar({ nombre: datos.nombre, email: datos.email }, datos.password);
    } catch (error) {
      setErrores(error.errores || {});
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      <div className="campo-formulario">
        <label htmlFor="admin-nombre">Nombre <span className="obligatorio">*</span></label>
        <input id="admin-nombre" name="nombre" value={datos.nombre} onChange={manejarCambio} maxLength={60} />
        {errores.nombre && <p className="texto-error">{errores.nombre}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="admin-email">Correo electrónico</label>
        <input type="email" id="admin-email" name="email" value={datos.email} onChange={manejarCambio} maxLength={100} />
        {errores.email && <p className="texto-error">{errores.email}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="admin-password">Contraseña <span className="obligatorio">*</span></label>
        <input type="password" id="admin-password" name="password" value={datos.password} onChange={manejarCambio} autoComplete="new-password" />
        {errores.password && <p className="texto-error">{errores.password}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="admin-confirmacion">Confirmar contraseña <span className="obligatorio">*</span></label>
        <input type="password" id="admin-confirmacion" name="confirmacion" value={datos.confirmacion} onChange={manejarCambio} autoComplete="new-password" />
        {errores.confirmacion && <p className="texto-error">{errores.confirmacion}</p>}
      </div>
      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={guardando}>Cancelar</button>
        <button type="submit" className="boton" disabled={guardando}>{guardando ? 'Guardando...' : 'Crear administrador'}</button>
      </div>
    </form>
  );
}
