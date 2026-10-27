import { useState } from 'react';
import { crearMaquina, actualizarMaquina } from '../../api/maquinas';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

const VALOR_INICIAL = { descripcion: '', estado: '' };

export default function MaquinaForm({ maquina, onGuardado }) {
  const [datos, setDatos] = useState(maquina
    ? { descripcion: maquina.descripcion, estado: maquina.estado || '' }
    : VALOR_INICIAL);
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
      if (maquina) {
        await actualizarMaquina(maquina.id, datos);
        notificar('exito', 'Máquina actualizada correctamente.');
      } else {
        await crearMaquina(datos);
        notificar('exito', 'Máquina creada correctamente.');
      }
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
        <label htmlFor="maq-descripcion">Descripción <span className="obligatorio">*</span></label>
        <input id="maq-descripcion" name="descripcion" value={datos.descripcion} onChange={manejarCambio}
               placeholder="Ej: Caminadora eléctrica" required />
        {errores.descripcion && <p className="texto-error">{errores.descripcion}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="maq-estado">Estado</label>
        <select id="maq-estado" name="estado" value={datos.estado} onChange={manejarCambio}>
          <option value="">Sin especificar</option>
          <option value="excelente">Excelente</option>
          <option value="bueno">Bueno</option>
          <option value="regular">Regular</option>
        </select>
        {errores.estado && <p className="texto-error">{errores.estado}</p>}
      </div>

      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Guardando...' : 'Guardar'}
      </button>
    </form>
  );
}
