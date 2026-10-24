import CursoCard from '../cards/CursoCard';
import Coleccion from '../../models/Coleccion';

/**
 * Lista de cursos. Recorre la colección con el patrón Iterator (Coleccion + for...of)
 * y propaga hacia arriba el evento onInscribirme de cada tarjeta.
 */
export default function CursosList({ cursos, cargando, error, onInscribirme }) {
  if (cargando) return <p>Cargando cursos...</p>;
  if (error) return <p className="texto-error">{error}</p>;

  const coleccion = new Coleccion(cursos);
  if (coleccion.estaVacia()) return <p>No hay cursos disponibles por el momento.</p>;

  const disponibles = coleccion.filtrar((curso) => curso.tieneCupos()).tamano;

  return (
    <>
      <p className="texto-ayuda">
        {disponibles} de {coleccion.tamano} cursos con cupos · {coleccion.sumar((curso) => curso.cupos)} cupos libres en total
      </p>
      <div className="rejilla">
        {coleccion.mapear((curso) => (
          <CursoCard key={curso.id} curso={curso} onInscribirme={onInscribirme} />
        ))}
      </div>
    </>
  );
}
