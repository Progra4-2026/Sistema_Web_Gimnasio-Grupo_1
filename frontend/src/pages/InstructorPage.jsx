import MaquinasList from '../components/lists/MaquinasList';
import RutinaTabla from '../components/lists/RutinaTabla';
import './Pages.css';

export default function InstructorPage() {
  return (
    <div className="contenedor seccion">
      <div className="titulo-seccion">
        <h1>Panel del instructor</h1>
        <p>Gestioná las máquinas del gimnasio y las rutinas asignadas a los clientes.</p>
      </div>

      <section className="seccion-servicio">
        <h2>Máquinas</h2>
        <MaquinasList soloLectura={false} />
      </section>

      <section className="seccion-servicio">
        <h2>Rutinas</h2>
        <RutinaTabla modo="instructor" />
      </section>
    </div>
  );
}
