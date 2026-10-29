import InstructoresList from '../../components/lists/InstructoresList';

/** Ruta /servicios/instructores */
export default function InstructoresPage() {
  return (
    <section className="seccion seccion-servicio">
      <h2>Instructores</h2>
      <p>Conocé a nuestro equipo de instructores certificados.</p>
      <InstructoresList />
    </section>
  );
}
