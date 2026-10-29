import { useState } from 'react';
import './Forms.css';

const SOLO_LETRAS = /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü' ]{2,30}$/;

function validar(datos, editando) {
  const errores = {};
  ['nombre', 'apellido1', 'apellido2'].forEach((campo) => {
    if (!SOLO_LETRAS.test(datos[campo].trim())) errores[campo] = 'Entre 2 y 30 letras.';
  });
  if (datos.direccion.length > 50) errores.direccion = 'Máximo 50 caracteres.';
  const email = datos.email.trim();
  if (email && (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) || email.length > 30)) errores.email = 'Correo válido de máximo 30 caracteres.';
  if (!/^[1-9]\d{7}$/.test(String(datos.telCel))) errores.telCel = 'Teléfono de 8 dígitos.';
  if (!datos.fechaContratacion) errores.fechaContratacion = 'Seleccioná la fecha de contratación.';
  else if (datos.fechaContratacion > new Date().toISOString().slice(0, 10)) errores.fechaContratacion = 'No puede ser una fecha futura.';
  if (!editando || datos.password) {
    if (datos.password.length < 8) errores.password = 'Mínimo 8 caracteres.';
    else if (datos.password !== datos.confirmacion) errores.confirmacion = 'Las contraseñas no coinciden.';
  }
  return errores;
}

/**
 * Crear o editar un instructor (Mantenimiento del administrador).
 * La contraseña es obligatoria al crear; al editar, dejarla vacía conserva la actual.
 * El backend la guarda cifrada con BCrypt.
 */
export default function InstructorForm({ instructor, onGuardar, onCancelar }) {
  const editando = Boolean(instructor);
  const [datos, setDatos] = useState({
    nombre: instructor?.nombre ?? '',
    apellido1: instructor?.apellido1 ?? '',
    apellido2: instructor?.apellido2 ?? '',
    direccion: instructor?.direccion ?? '',
    email: instructor?.email ?? '',
    telCel: instructor ? String(instructor.telCel) : '',
    fechaContratacion: instructor?.fechaContratacion ?? '',
    password: '',
    confirmacion: '',
  });
  const [errores, setErrores] = useState({});
  const [guardando, setGuardando] = useState(false);

  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setDatos((anterior) => ({ ...anterior, [name]: value }));
  }

  async function manejarEnvio(evento) {
    evento.preventDefault();
    const encontrados = validar(datos, editando);
    setErrores(encontrados);
    if (Object.keys(encontrados).length > 0) return;

    setGuardando(true);
    try {
      const { password, ...resto } = datos;
      delete resto.confirmacion;
      await onGuardar(resto, password);
    } catch (error) {
      setErrores(error.errores || {});
    } finally {
      setGuardando(false);
    }
  }

  const campo = (name, etiqueta, props = {}, obligatorio = true) => (
    <div className="campo-formulario">
      <label htmlFor={`instructor-${name}`}>{etiqueta} {obligatorio && <span className="obligatorio">*</span>}</label>
      <input id={`instructor-${name}`} name={name} value={datos[name]} onChange={manejarCambio} {...props} />
      {errores[name] && <p className="texto-error">{errores[name]}</p>}
    </div>
  );

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      {editando && (
        <p className="texto-ayuda">Instructor #{instructor.codInstructor} (el código es su usuario y no se puede modificar)</p>
      )}
      {campo('nombre', 'Nombre', { maxLength: 30 })}
      {campo('apellido1', 'Primer apellido', { maxLength: 30 })}
      {campo('apellido2', 'Segundo apellido', { maxLength: 30 })}
      {campo('direccion', 'Dirección', { maxLength: 50 }, false)}
      {campo('email', 'Correo electrónico', { type: 'email', maxLength: 30 }, false)}
      {campo('telCel', 'Celular', { inputMode: 'numeric', maxLength: 8 })}
      {campo('fechaContratacion', 'Fecha de contratación', { type: 'date' })}
      {campo('password', editando ? 'Nueva contraseña (opcional)' : 'Contraseña', { type: 'password', autoComplete: 'new-password' }, !editando)}
      {campo('confirmacion', 'Confirmar contraseña', { type: 'password', autoComplete: 'new-password' }, !editando)}

      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={guardando}>Cancelar</button>
        <button type="submit" className="boton" disabled={guardando}>{guardando ? 'Guardando...' : 'Guardar'}</button>
      </div>
    </form>
  );
}
