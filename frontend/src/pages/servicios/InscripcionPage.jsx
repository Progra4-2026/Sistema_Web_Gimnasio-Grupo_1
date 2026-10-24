import { useLocation, useNavigate } from 'react-router-dom';
import { useCursos } from '../../context/CursosContext';
import InscripcionForm from '../../components/forms/InscripcionForm';

/** Ruta /servicios/inscripcion */
export default function InscripcionPage() {
  const { cursos, recargarCursos } = useCursos();
  const location = useLocation();
  const navigate = useNavigate();

  // Si se llegó desde una tarjeta de curso ("Inscribirme"), viene el id del curso elegido
  const cursoSeleccionadoId = location.state?.idCurso ?? null;

  // Evento que sube desde InscripcionForm: se recargan los cupos y se limpia el curso elegido
  function manejarInscripcionExitosa() {
    recargarCursos();
    navigate('.', { replace: true, state: null });
  }

  return (
    <section className="seccion seccion-servicio">
      <h2>Inscripción</h2>
      <p>Completá tus datos para inscribirte a una rutina o a un curso.</p>
      <InscripcionForm
        cursos={cursos}
        cursoSeleccionadoId={cursoSeleccionadoId}
        onInscripcionExitosa={manejarInscripcionExitosa}
      />
    </section>
  );
}
