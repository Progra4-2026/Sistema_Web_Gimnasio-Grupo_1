import { useCursos } from '../../context/CursosContext';
import DesinscripcionForm from '../../components/forms/DesinscripcionForm';

/** Ruta /servicios/desinscribirse */
export default function DesinscripcionPage() {
  const { recargarCursos } = useCursos();

  return (
    <section className="seccion seccion-servicio">
      <h2>Desinscribirse</h2>
      <p>Podés retirar tu inscripción del gimnasio siempre y cuando te encontrés al día con tus pagos.</p>
      {/* Evento que sube desde el formulario: al desinscribirse se libera el cupo del curso */}
      <DesinscripcionForm onDesinscripcionExitosa={recargarCursos} />
    </section>
  );
}
