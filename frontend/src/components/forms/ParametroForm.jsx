import { useState } from 'react';
import './Forms.css';

/** Editar el valor de un parámetro del sistema. La clave es la llave primaria y no se modifica. */
export default function ParametroForm({ parametro, onGuardar, onCancelar }) {
  const [valor, setValor] = useState(parametro.valor);
  const [error, setError] = useState('');
  const [guardando, setGuardando] = useState(false);
  const numerico = /^\d+$/.test(parametro.valor);

  async function manejarEnvio(evento) {
    evento.preventDefault();
    const limpio = valor.trim();
    if (!limpio || limpio.length > 255) return setError('El valor es obligatorio (máximo 255 caracteres).');
    if (numerico && !/^[1-9]\d{0,8}$/.test(limpio)) return setError('Debe ser un número entero mayor que 0.');
    setError('');
    setGuardando(true);
    try {
      await onGuardar(limpio);
    } catch (err) {
      setError(err.errores?.valor || err.message);
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      <p className="texto-ayuda">Parámetro <strong>{parametro.clave}</strong>: {parametro.descripcion}</p>
      <div className="campo-formulario">
        <label htmlFor="parametro-valor">Valor <span className="obligatorio">*</span></label>
        <input id="parametro-valor" value={valor} onChange={(e) => setValor(e.target.value)} maxLength={255} inputMode={numerico ? 'numeric' : undefined} />
        {error && <p className="texto-error">{error}</p>}
      </div>
      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={guardando}>Cancelar</button>
        <button type="submit" className="boton" disabled={guardando}>{guardando ? 'Guardando...' : 'Guardar'}</button>
      </div>
    </form>
  );
}
