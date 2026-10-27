import { useEffect, useState } from 'react';
import { listarMaquinas } from '../../api/maquinas';
import { crearRutina, actualizarRutina } from '../../api/rutinas';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

const VALOR_INICIAL = { cliente: '', maquina: '', fecha: '', horas: '' };

export default function RutinaForm({ rutina, onGuardado }) {
  const [datos, setDatos] = useState(rutina
    ? { cliente: rutina.cliente, maquina: rutina.maquina, fecha: rutina.fecha, horas: rutina.horas }
    : VALOR_INICIAL);
  const [maquinas, setMaquinas] = useState([]);
  const [errores, setErrores] = useState({});
  const [enviando, setEnviando] = useState(false);
  const { notificar } = useNotificacion();

  useEffect(() => {
    listarMaquinas().then(setMaquinas).catch(() => setMaquinas([]));
  }, []);

  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setDatos((anterior) => ({ ...anterior, [name]: value }));
  }

  async function manejarEnvio(evento) {
    evento.preventDefault();
    setEnviando(true);
    try {
      const cuerpo = {
        cliente: Number(datos.cliente),
        maquina: Number(datos.maquina),
        fecha: datos.fecha,
        horas: Number(datos.horas),
      };
      if (rutina) {
        await actualizarRutina(rutina.id, cuerpo);
        notificar('exito', 'Rutina actualizada correctamente.');
      } else {
        await crearRutina(cuerpo);
        notificar('exito', 'Rutina creada correctamente.');
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
        <label htmlFor="rut-cliente">Cédula del cliente <span className="obligatorio">*</span></label>
        <input id="rut-cliente" name="cliente" value={datos.cliente} onChange={manejarCambio} placeholder="Ej: 304560789" required />
        {errores.cliente && <p className="texto-error">{errores.cliente}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="rut-maquina">Máquina <span className="obligatorio">*</span></label>
        <select id="rut-maquina" name="maquina" value={datos.maquina} onChange={manejarCambio} required>
          <option value="">Seleccione una máquina</option>
          {maquinas.map((m) => (
            <option key={m.id} value={m.id}>{m.descripcion}</option>
          ))}
        </select>
        {errores.maquina && <p className="texto-error">{errores.maquina}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="rut-fecha">Fecha <span className="obligatorio">*</span></label>
        <input type="date" id="rut-fecha" name="fecha" value={datos.fecha} onChange={manejarCambio} required />
        {errores.fecha && <p className="texto-error">{errores.fecha}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="rut-horas">Horas <span className="obligatorio">*</span></label>
        <input type="number" id="rut-horas" name="horas" min="1" max="24" value={datos.horas} onChange={manejarCambio} required />
        {errores.horas && <p className="texto-error">{errores.horas}</p>}
      </div>

      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Guardando...' : 'Guardar'}
      </button>
    </form>
  );
}
