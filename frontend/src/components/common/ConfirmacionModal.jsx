import Modal from './Modal';

/**
 * Diálogo de confirmación reutilizable (borrar un curso, un cliente, etc.).
 * Avisa al componente padre con onConfirmar u onCancelar.
 */
export default function ConfirmacionModal({ abierto, titulo, mensaje, textoConfirmar = 'Sí, eliminar', procesando, onConfirmar, onCancelar }) {
  return (
    <Modal abierto={abierto} titulo={titulo} onCerrar={onCancelar}>
      <p>{mensaje}</p>
      <div className="modal-acciones">
        <button type="button" className="boton boton-secundario" onClick={onCancelar} disabled={procesando}>
          Cancelar
        </button>
        <button type="button" className="boton boton-peligro" onClick={onConfirmar} disabled={procesando}>
          {procesando ? 'Procesando...' : textoConfirmar}
        </button>
      </div>
    </Modal>
  );
}
