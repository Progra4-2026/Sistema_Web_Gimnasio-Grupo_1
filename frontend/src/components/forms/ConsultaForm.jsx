import { useState } from 'react';
import { crearConsulta } from '../../api/consultas';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

const VALOR_INICIAL = { nombre: '', email: '', telefono: '', asunto: 'general', mensaje: '' };

function validar(datos) {
  const errores = {};
  if (datos.nombre.trim().length < 3) errores.nombre = 'Ingresá tu nombre completo.';
  if (!/^\S+@\S+\.\S+$/.test(datos.email)) errores.email = 'Ingresá un correo válido.';
  if (!/^[0-9]{4}-?[0-9]{4}$/.test(datos.telefono)) errores.telefono = 'Formato: 8888-8888.';
  if (datos.mensaje.trim().length < 10) errores.mensaje = 'Contanos un poco más (mínimo 10 caracteres).';
  return errores;
}

export default function ConsultaForm() {
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
    const erroresEncontrados = validar(datos);
    setErrores(erroresEncontrados);
    if (Object.keys(erroresEncontrados).length > 0) return;

    setEnviando(true);
    try {
      await crearConsulta(datos);
      notificar('exito', 'Consulta enviada. Te responderemos a la brevedad.');
      setDatos(VALOR_INICIAL);
      setErrores({});
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
        <label htmlFor="nombre">Nombre completo <span className="obligatorio">*</span></label>
        <input id="nombre" name="nombre" value={datos.nombre} onChange={manejarCambio} placeholder="Ej: María Rodríguez Solano" />
        {errores.nombre && <p className="texto-error">{errores.nombre}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="email">Correo electrónico <span className="obligatorio">*</span></label>
        <input id="email" name="email" value={datos.email} onChange={manejarCambio} placeholder="ejemplo@correo.com" />
        {errores.email && <p className="texto-error">{errores.email}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="telefono">Teléfono <span className="obligatorio">*</span></label>
        <input id="telefono" name="telefono" value={datos.telefono} onChange={manejarCambio} placeholder="8888-8888" />
        {errores.telefono && <p className="texto-error">{errores.telefono}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="asunto">Asunto</label>
        <select id="asunto" name="asunto" value={datos.asunto} onChange={manejarCambio}>
          <option value="general">Consulta general</option>
          <option value="inscripcion">Inscripciones</option>
          <option value="cursos">Cursos y horarios</option>
          <option value="soporte">Soporte / Reclamo</option>
        </select>
      </div>

      <div className="campo-formulario">
        <label htmlFor="mensaje">Mensaje <span className="obligatorio">*</span></label>
        <textarea id="mensaje" name="mensaje" rows="5" value={datos.mensaje} onChange={manejarCambio} placeholder="Escribí tu consulta aquí..." />
        {errores.mensaje && <p className="texto-error">{errores.mensaje}</p>}
      </div>

      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Enviando...' : 'Enviar consulta'}
      </button>
    </form>
  );
}
