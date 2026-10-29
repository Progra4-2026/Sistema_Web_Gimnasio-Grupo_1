import { useState } from 'react';
import './Forms.css';

function validar(datos, editando) {
  const errores = {};
  if (!editando) {
    if (!/^\d{1,12}$/.test(String(datos.cliente).trim())) errores.cliente = 'Ingresá la cédula del cliente (solo números).';
    if (!datos.maquina) errores.maquina = 'Seleccioná una máquina.';
  }
  if (!/^\d{4}-\d{2}-\d{2}$/.test(datos.fecha)) errores.fecha = 'Seleccioná una fecha.';
  const horas = Number(datos.horas);
  if (datos.horas === '' || !Number.isInteger(horas) || horas < 1 || horas > 24) errores.horas = 'Número entero entre 1 y 24.';
  return errores;
}

/**
 * Asignar o editar la rutina de un cliente (Panel del instructor).
 * Al editar, el cliente y la máquina quedan bloqueados: son llaves foráneas y el backend no permite cambiarlas.
 * El instructor no se elige: el backend usa el instructor con sesión iniciada.
 */
export default function RutinaForm({ rutina, maquinas, onGuardar, onCancelar }) {
  const editando = Boolean(rutina);
  const [datos, setDatos] = useState({
    cliente: rutina?.cliente ?? '',
    maquina: rutina?.maquina ?? '',
    fecha: rutina?.fecha ?? new Date().toISOString().slice(0, 10),
    horas: rutina ? String(rutina.horas) : '1',
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
      await onGuardar({ ...datos, cliente: String(datos.cliente).trim() });
    } catch (error) {
      setErrores(error.errores || {});
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      {editando && (
        <p className="texto-ayuda">
          Rutina #{rutina.id}. El cliente y la máquina no se pueden cambiar; para eso eliminá la rutina y creá una nueva.
        </p>
      )}

      <div className="campo-formulario">
        <label htmlFor="rutina-cliente">Cédula del cliente <span className="obligatorio">*</span></label>
        <input id="rutina-cliente" name="cliente" value={datos.cliente} onChange={manejarCambio} disabled={editando} inputMode="numeric" placeholder="Ej: 201" />
        {editando && rutina.clienteNombre && <p className="texto-ayuda">{rutina.clienteNombre}</p>}
        {errores.cliente && <p className="texto-error">{errores.cliente}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="rutina-maquina">Máquina <span className="obligatorio">*</span></label>
        <select id="rutina-maquina" name="maquina" value={datos.maquina} onChange={manejarCambio} disabled={editando}>
          <option value="">Seleccioná una máquina</option>
          {maquinas.map((m) => (
            <option key={m.id} value={m.id} disabled={!m.disponible && !editando}>
              {m.descripcion} ({m.estado})
            </option>
          ))}
        </select>
        {errores.maquina && <p className="texto-error">{errores.maquina}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="rutina-fecha">Fecha <span className="obligatorio">*</span></label>
        <input type="date" id="rutina-fecha" name="fecha" value={datos.fecha} onChange={manejarCambio} />
        {errores.fecha && <p className="texto-error">{errores.fecha}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="rutina-horas">Horas <span className="obligatorio">*</span></label>
        <input type="number" id="rutina-horas" name="horas" min="1" max="24" value={datos.horas} onChange={manejarCambio} />
        {errores.horas && <p className="texto-error">{errores.horas}</p>}
      </div>

      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={guardando}>Cancelar</button>
        <button type="submit" className="boton" disabled={guardando}>{guardando ? 'Guardando...' : 'Guardar'}</button>
      </div>
    </form>
  );
}
