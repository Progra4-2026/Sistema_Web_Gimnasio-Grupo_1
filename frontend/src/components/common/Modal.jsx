import './Modal.css';

export default function Modal({ abierto, titulo, onCerrar, children }) {
  if (!abierto) return null;

  return (
    <div className="modal-fondo" onClick={onCerrar}>
      <div className="modal-caja" onClick={(evento) => evento.stopPropagation()}>
        <div className="modal-encabezado">
          <h3>{titulo}</h3>
          <button type="button" className="modal-cerrar" aria-label="Cerrar" onClick={onCerrar}>
            ×
          </button>
        </div>
        <div className="modal-cuerpo">{children}</div>
      </div>
    </div>
  );
}
