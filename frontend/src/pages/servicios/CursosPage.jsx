import { useNavigate } from 'react-router-dom';
import { useCursos } from '../../context/CursosContext';
import CursosList from '../../components/lists/CursosList';

/** Ruta /servicios/cursos */
export default function CursosPage() {
  const { cursos, cargando, error } = useCursos();
  const navigate = useNavigate();

  // Evento que sube desde CursoCard → CursosList → aquí: abre Inscripción con el curso ya elegido
  function manejarInscribirme(curso) {
    navigate('/servicios/inscripcion', { state: { idCurso: curso.id } });
  }

  return (
    <section className="seccion seccion-servicio">
      <h2>Cursos</h2>
      <p>Elegí el curso grupal que más se adapte a vos.</p>
      <CursosList cursos={cursos} cargando={cargando} error={error} onInscribirme={manejarInscribirme} />
    </section>
  );
}
