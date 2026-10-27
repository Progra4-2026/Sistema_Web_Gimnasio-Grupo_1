import './Cards.css';

export default function MaquinaCard({ maquina, onEditar, onEliminar }) {
  return (
    <article className="tarjeta tarjeta-maquina">
      <h3>{maquina.descripcion}</h3>
      {maquina.estado && <span className={`etiqueta-estado etiqueta-estado--${maquina.estado}`}>{maquina.estado}</span>}
      {(onEditar || onEliminar) && (
        <div className="modal-acciones">
          {onEditar && (
            <button type="button" className="boton boton-secundario" onClick={() => onEditar(maquina)}>Editar</button>
          )}
          {onEliminar && (
            <button type="button" className="boton boton-peligro" onClick={() => onEliminar(maquina.id)}>Eliminar</button>
          )}
        </div>
      )}
    </article>
  );
}
