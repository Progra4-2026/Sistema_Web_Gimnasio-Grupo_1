import MaquinasList from '../../components/lists/MaquinasList';

/** Ruta /servicios/maquinas */
export default function MaquinasPage() {
  return (
    <section className="seccion seccion-servicio">
      <h2>Máquinas</h2>
      <p>Listado de máquinas y equipo disponible en nuestras instalaciones.</p>
      <MaquinasList />
    </section>
  );
}
