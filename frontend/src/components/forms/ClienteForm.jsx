import { useState } from 'react';
import './Forms.css';

const OBJETIVOS_RUTINA = [
  { valor: 'fuerza', texto: 'Ganar fuerza y masa muscular' },
  { valor: 'resistencia', texto: 'Mejorar la resistencia cardiovascular' },
  { valor: 'peso', texto: 'Bajar de peso' },
  { valor: 'flexibilidad', texto: 'Flexibilidad y movilidad' },
];

function validar(datos) {
  const errores = {};
  const nombre = datos.nombre.trim();
  if (nombre.length < 3 || nombre.length > 80) errores.nombre = 'Debe tener entre 3 y 80 caracteres.';
  if (!/^\S+@\S+\.\S+$/.test(datos.email)) errores.email = 'Ingresá un correo válido.';
  if (!/^[0-9]{4}-?[0-9]{4}$/.test(datos.telefono)) errores.telefono = 'Formato: 8888-8888.';
  if (!datos.fechaNacimiento) errores.fechaNacimiento = 'Ingresá la fecha de nacimiento.';
  return errores;
}

/**
 * Formulario para editar los datos de contacto de un cliente (Mantenimiento).
 * La cédula (llave primaria) y el curso (llave foránea) no se pueden cambiar, por eso solo se muestran.
 */
export default function ClienteForm({ cliente, onGuardar, onCancelar }) {
  const [datos, setDatos] = useState({
    nombre: cliente.nombre ?? '',
    email: cliente.email ?? '',
    telefono: cliente.telefono ?? '',
    fechaNacimiento: cliente.fechaNacimiento ?? '',
    objetivo: cliente.objetivo ?? '',
  });
  const [errores, setErrores] = useState({});
  const [guardando, setGuardando] = useState(false);
  const esRutina = cliente.tipoInscripcion === 'rutina';

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
      await onGuardar({ ...datos, nombre: datos.nombre.trim(), objetivo: esRutina ? datos.objetivo : null });
    } catch (error) {
      setErrores(error.errores || {});
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      <p className="texto-ayuda">
        Cédula {cliente.cedula} · {esRutina ? 'Rutina personalizada' : `Curso #${cliente.idCurso}`} (no se pueden modificar)
      </p>

      <div className="campo-formulario">
        <label htmlFor="cli-nombre">Nombre completo <span className="obligatorio">*</span></label>
        <input id="cli-nombre" name="nombre" value={datos.nombre} onChange={manejarCambio} maxLength={80} />
        {errores.nombre && <p className="texto-error">{errores.nombre}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="cli-email">Correo electrónico <span className="obligatorio">*</span></label>
        <input id="cli-email" name="email" value={datos.email} onChange={manejarCambio} />
        {errores.email && <p className="texto-error">{errores.email}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="cli-telefono">Teléfono <span className="obligatorio">*</span></label>
        <input id="cli-telefono" name="telefono" value={datos.telefono} onChange={manejarCambio} placeholder="8888-8888" />
        {errores.telefono && <p className="texto-error">{errores.telefono}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="cli-nacimiento">Fecha de nacimiento <span className="obligatorio">*</span></label>
        <input type="date" id="cli-nacimiento" name="fechaNacimiento" value={datos.fechaNacimiento} onChange={manejarCambio} />
        {errores.fechaNacimiento && <p className="texto-error">{errores.fechaNacimiento}</p>}
      </div>

      {esRutina && (
        <div className="campo-formulario">
          <label htmlFor="cli-objetivo">Objetivo</label>
          <select id="cli-objetivo" name="objetivo" value={datos.objetivo} onChange={manejarCambio}>
            {OBJETIVOS_RUTINA.map((o) => <option key={o.valor} value={o.valor}>{o.texto}</option>)}
          </select>
          {errores.objetivo && <p className="texto-error">{errores.objetivo}</p>}
        </div>
      )}

      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={guardando}>Cancelar</button>
        <button type="submit" className="boton" disabled={guardando}>{guardando ? 'Guardando...' : 'Guardar cambios'}</button>
      </div>
    </form>
  );
}
