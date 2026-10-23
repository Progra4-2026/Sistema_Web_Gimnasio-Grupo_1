import { useState } from 'react';
import { eliminarInscripcion } from '../../api/inscripciones';
import { useNotificacion } from '../../context/NotificationContext';
import Modal from '../common/Modal';
import './Forms.css';

export default function DesinscripcionForm({ onDesinscripcionExitosa }) {
  const [cedula, setCedula] = useState('');
  const [error, setError] = useState('');
  const [confirmacionAbierta, setConfirmacionAbierta] = useState(false);
  const [enviando, setEnviando] = useState(false);
  const { notificar } = useNotificacion();

  function manejarEnvio(evento) {
    evento.preventDefault();
    if (!/^[0-9]{9,12}$/.test(cedula)) {
      setError('Ingresá una cédula válida (9 a 12 dígitos).');
      return;
    }
    setError('');
    setConfirmacionAbierta(true);
  }

  async function confirmarBaja() {
    setEnviando(true);
    try {
      await eliminarInscripcion(cedula);
      notificar('exito', 'Tu inscripción fue eliminada correctamente.');
      setCedula('');
      onDesinscripcionExitosa?.(); // avisa al componente padre (ServiciosPage) para recargar los cupos
    } catch (err) {
      notificar('error', err.message);
    } finally {
      setEnviando(false);
      setConfirmacionAbierta(false);
    }
  }

  return (
    <>
      <form className="formulario" onSubmit={manejarEnvio}>
        <div className="campo-formulario">
          <label htmlFor="desin-cedula">Cédula / Identificación <span className="obligatorio">*</span></label>
          <input id="desin-cedula" value={cedula} onChange={(evento) => setCedula(evento.target.value)} placeholder="Ej: 304560789" />
          {error && <p className="texto-error">{error}</p>}
        </div>
        <p className="texto-ayuda">Nota: la solicitud solo se procesará si no existen pagos pendientes asociados a tu cuenta.</p>
        <button type="submit" className="boton boton-peligro">Confirmar desinscripción</button>
      </form>

      <Modal abierto={confirmacionAbierta} titulo="Confirmar desinscripción" onCerrar={() => setConfirmacionAbierta(false)}>
        <p>¿Estás seguro de que deseás eliminar la inscripción con cédula {cedula}? Esta acción no se puede deshacer.</p>
        <div className="modal-acciones">
          <button type="button" className="boton boton-secundario" onClick={() => setConfirmacionAbierta(false)}>
            Cancelar
          </button>
          <button type="button" className="boton boton-peligro" onClick={confirmarBaja} disabled={enviando}>
            {enviando ? 'Eliminando...' : 'Sí, eliminar'}
          </button>
        </div>
      </Modal>
    </>
  );
}
