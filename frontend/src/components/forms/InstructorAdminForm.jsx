import { useState } from 'react';
import { crearInstructor } from '../../api/admin';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

const VALOR_INICIAL = {
  codInstructor: '', nombre: '', apellido1: '', apellido2: '',
  email: '', telCel: '', fechaContratacion: '', password: '',
};

export default function InstructorAdminForm({ onGuardado }) {
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
      await crearInstructor({
        ...datos,
        codInstructor: Number(datos.codInstructor),
        telCel: datos.telCel ? Number(datos.telCel) : null,
      });
      notificar('exito', 'Instructor creado correctamente.');
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
        <label htmlFor="inst-cod">Código <span className="obligatorio">*</span></label>
        <input id="inst-cod" name="codInstructor" value={datos.codInstructor} onChange={manejarCambio} required />
        {errores.codInstructor && <p className="texto-error">{errores.codInstructor}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="inst-nombre">Nombre <span className="obligatorio">*</span></label>
        <input id="inst-nombre" name="nombre" value={datos.nombre} onChange={manejarCambio} required />
        {errores.nombre && <p className="texto-error">{errores.nombre}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="inst-ap1">Apellido 1 <span className="obligatorio">*</span></label>
        <input id="inst-ap1" name="apellido1" value={datos.apellido1} onChange={manejarCambio} required />
        {errores.apellido1 && <p className="texto-error">{errores.apellido1}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="inst-ap2">Apellido 2 <span className="obligatorio">*</span></label>
        <input id="inst-ap2" name="apellido2" value={datos.apellido2} onChange={manejarCambio} required />
        {errores.apellido2 && <p className="texto-error">{errores.apellido2}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="inst-email">Correo electrónico</label>
        <input id="inst-email" name="email" value={datos.email} onChange={manejarCambio} />
        {errores.email && <p className="texto-error">{errores.email}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="inst-tel">Teléfono celular</label>
        <input id="inst-tel" name="telCel" value={datos.telCel} onChange={manejarCambio} />
      </div>
      <div className="campo-formulario">
        <label htmlFor="inst-fecha">Fecha de contratación <span className="obligatorio">*</span></label>
        <input type="date" id="inst-fecha" name="fechaContratacion" value={datos.fechaContratacion} onChange={manejarCambio} required />
        {errores.fechaContratacion && <p className="texto-error">{errores.fechaContratacion}</p>}
      </div>
      <div className="campo-formulario">
        <label htmlFor="inst-password">Contraseña</label>
        <input type="password" id="inst-password" name="password" value={datos.password} onChange={manejarCambio}
               placeholder="Por defecto: el código" />
        <span className="texto-ayuda">Se almacenará cifrada. Si se deja vacío, queda el código como contraseña.</span>
      </div>
      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Guardando...' : 'Crear instructor'}
      </button>
    </form>
  );
}
