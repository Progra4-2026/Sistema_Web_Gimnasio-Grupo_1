import './Cards.css';

// El backend envía solo el nombre del archivo (ej. "clase_spinning.jpg"); las imágenes están en /assets
function rutaImagen(imagen) {
  if (!imagen) return null;
  return imagen.startsWith('/') || imagen.startsWith('http') ? imagen : `/assets/${imagen}`;
}

export default function CursoCard({ curso, onInscribirme }) {
  const sinCupos = Number(curso.cupos) <= 0;
  const imagen = rutaImagen(curso.imagen);

  return (
    <article className="tarjeta tarjeta-curso">
      {imagen && <img src={imagen} alt={curso.descripcion} />}
      <div className="tarjeta-curso__cuerpo">
        <p className="etiqueta-id">Curso #{curso.id}</p>
        <h3>{curso.descripcion}</h3>
        <p>{curso.detalle}</p>
        <ul className="lista-detalles">
          <li><strong>Horario:</strong> {curso.horario}</li>
          <li>
            <strong>Disponibilidad:</strong>{' '}
            {sinCupos ? <span className="sin-cupos">Sin cupos</span> : `${curso.cupos} cupos`}
          </li>
        </ul>
        <button
          type="button"
          className="boton"
          disabled={sinCupos}
          onClick={() => onInscribirme(curso)}
        >
          Inscribirme
        </button>
      </div>
    </article>
  );
}
