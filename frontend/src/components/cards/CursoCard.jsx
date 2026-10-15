import './Cards.css';

export default function CursoCard({ curso, onInscribirme }) {
  const sinCupos = Number(curso.cupos) <= 0;

  return (
    <article className="tarjeta tarjeta-curso">
      {curso.imagen && <img src={curso.imagen} alt={curso.descripcion} />}
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
