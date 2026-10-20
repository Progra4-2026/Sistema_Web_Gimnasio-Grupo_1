import CursoCard from '../cards/CursoCard';

export default function CursosList({ cursos, cargando, error, onInscribirme }) {
  if (cargando) return <p>Cargando cursos...</p>;
  if (error) return <p className="texto-error">{error}</p>;
  if (cursos.length === 0) return <p>No hay cursos disponibles por el momento.</p>;

  return (
    <div className="rejilla">
      {cursos.map((curso) => (
        <CursoCard key={curso.id} curso={curso} onInscribirme={onInscribirme} />
      ))}
    </div>
  );
}
