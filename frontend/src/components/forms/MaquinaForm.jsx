import { useState } from 'react';
import { ESTADOS_MAQUINA } from '../../models/Maquina';
import './Forms.css';

function validar(datos) {
  const errores = {};
  const descripcion = datos.descripcion.trim();
  if (descripcion.length < 3 || descripcion.length > 50) errores.descripcion = 'Debe tener entre 3 y 50 caracteres.';
  if (!ESTADOS_MAQUINA.includes(datos.estado)) errores.estado = 'Elegí un estado de la lista.';
  return errores;
}

/**
 * Crear o editar una máquina (Panel del instructor).
 * Entrega los datos al padre con onGuardar; si el backend responde con errores por campo, se muestran aquí.
 */
export default function MaquinaForm({ maquina, onGuardar, onCancelar }) {
  const [datos, setDatos] = useState({ descripcion: maquina?.descripcion ?? '', estado: maquina?.estado ?? 'bueno' });
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
      await onGuardar({ descripcion: datos.descripcion.trim(), estado: datos.estado });
    } catch (error) {
      setErrores(error.errores || {});
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      {maquina && <p className="texto-ayuda">Máquina #{maquina.id} (el identificador no se puede modificar)</p>}

      <div className="campo-formulario">
        <label htmlFor="maquina-descripcion">Descripción <span className="obligatorio">*</span></label>
        <input id="maquina-descripcion" name="descripcion" value={datos.descripcion} onChange={manejarCambio} maxLength={50} placeholder="Ej: caminadora" />
        {errores.descripcion && <p className="texto-error">{errores.descripcion}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="maquina-estado">Estado <span className="obligatorio">*</span></label>
        <select id="maquina-estado" name="estado" value={datos.estado} onChange={manejarCambio}>
          {ESTADOS_MAQUINA.map((estado) => <option key={estado} value={estado}>{estado}</option>)}
        </select>
        {errores.estado && <p className="texto-error">{errores.estado}</p>}
      </div>

      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={guardando}>Cancelar</button>
        <button type="submit" className="boton" disabled={guardando}>{guardando ? 'Guardando...' : 'Guardar'}</button>
      </div>
    </form>
  );
}
