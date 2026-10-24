import { useState } from 'react';
import './Forms.css';

// Imágenes disponibles en public/assets (el backend guarda solo el nombre del archivo)
const IMAGENES = [
  'clase_spinning.jpg', 'clase_yoga.jpg', 'clase_crossfit.jpg', 'clases_grupales.jpg',
  'area_pesas.jpg', 'zona-cardio.jpg',
];

function validar(datos) {
  const errores = {};
  const descripcion = datos.descripcion.trim();
  if (descripcion.length < 3 || descripcion.length > 50) errores.descripcion = 'Debe tener entre 3 y 50 caracteres.';
  if (datos.detalle.length > 300) errores.detalle = 'Máximo 300 caracteres.';
  const horario = datos.horario.trim();
  if (horario.length < 3 || horario.length > 60) errores.horario = 'Debe tener entre 3 y 60 caracteres.';
  const cupos = Number(datos.cupos);
  if (datos.cupos === '' || !Number.isInteger(cupos) || cupos < 0 || cupos > 100) errores.cupos = 'Número entero entre 0 y 100.';
  return errores;
}

/**
 * Formulario para crear o editar un curso (Mantenimiento).
 * No llama a la API directamente: entrega los datos al padre con onGuardar, que hace el POST/PUT.
 * Si el backend responde con errores por campo, se muestran aquí.
 */
export default function CursoForm({ curso, onGuardar, onCancelar }) {
  const [datos, setDatos] = useState({
    descripcion: curso?.descripcion ?? '',
    detalle: curso?.detalle ?? '',
    horario: curso?.horario ?? '',
    cupos: curso ? String(curso.cupos) : '',
    imagen: curso?.imagen ?? IMAGENES[0],
  });
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
      await onGuardar({ ...datos, descripcion: datos.descripcion.trim(), horario: datos.horario.trim(), cupos: Number(datos.cupos) });
    } catch (error) {
      setErrores(error.errores || {});
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      {curso && <p className="texto-ayuda">Curso #{curso.id} (el identificador no se puede modificar)</p>}

      <div className="campo-formulario">
        <label htmlFor="curso-descripcion">Nombre <span className="obligatorio">*</span></label>
        <input id="curso-descripcion" name="descripcion" value={datos.descripcion} onChange={manejarCambio} maxLength={50} />
        {errores.descripcion && <p className="texto-error">{errores.descripcion}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="curso-detalle">Descripción</label>
        <textarea id="curso-detalle" name="detalle" rows="3" value={datos.detalle} onChange={manejarCambio} maxLength={300} />
        {errores.detalle && <p className="texto-error">{errores.detalle}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="curso-horario">Horario <span className="obligatorio">*</span></label>
        <input id="curso-horario" name="horario" value={datos.horario} onChange={manejarCambio} placeholder="Ej: Lun/Mié/Vie, 6:00 a.m." maxLength={60} />
        {errores.horario && <p className="texto-error">{errores.horario}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="curso-cupos">Cupos disponibles <span className="obligatorio">*</span></label>
        <input type="number" id="curso-cupos" name="cupos" min="0" max="100" value={datos.cupos} onChange={manejarCambio} />
        {errores.cupos && <p className="texto-error">{errores.cupos}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="curso-imagen">Imagen</label>
        <select id="curso-imagen" name="imagen" value={datos.imagen} onChange={manejarCambio}>
          {IMAGENES.map((imagen) => <option key={imagen} value={imagen}>{imagen}</option>)}
        </select>
        {errores.imagen && <p className="texto-error">{errores.imagen}</p>}
      </div>

      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={guardando}>Cancelar</button>
        <button type="submit" className="boton" disabled={guardando}>{guardando ? 'Guardando...' : 'Guardar'}</button>
      </div>
    </form>
  );
}
