import { Link } from 'react-router-dom';
import InstalacionCard from '../components/cards/InstalacionCard';
import './Pages.css';

const INSTALACIONES = [
  { titulo: 'Área de pesas y máquinas de fuerza', imagen: '/assets/area_pesas.jpg' },
  { titulo: 'Zona de cardio', imagen: '/assets/zona-cardio.jpg' },
  { titulo: 'Salón de clases grupales', imagen: '/assets/clases_grupales.jpg' },
  { titulo: 'Vestidores y casilleros', imagen: '/assets/vestidores.jpg' },
];

export default function InicioPage() {
  return (
    <>
      <section className="hero-inicio">
        <div className="contenedor hero-inicio__contenido">
          <h1>Bienvenido a Gimnasio PowerFit</h1>
          <p>
            Más de 10 años ayudando a nuestra comunidad a alcanzar sus metas físicas. Rutinas
            personalizadas, cursos grupales e instructores certificados.
          </p>
          <div className="hero-inicio__botones">
            <Link to="/servicios/inscripcion" className="boton">Inscribirme ahora</Link>
            <Link to="/servicios/cursos" className="boton boton-secundario">Ver cursos</Link>
          </div>
        </div>
      </section>

      <section className="seccion contenedor">
        <div className="titulo-seccion">
          <h2>Nuestras instalaciones</h2>
          <p>Conocé los espacios con los que contamos para que entrenes cómodo y seguro.</p>
        </div>

        <div className="rejilla">
          {INSTALACIONES.map((instalacion) => (
            <InstalacionCard key={instalacion.titulo} titulo={instalacion.titulo} imagen={instalacion.imagen} />
          ))}
        </div>
      </section>

      <section className="seccion franja-info">
        <div className="contenedor franja-info__grid">
          <article className="tarjeta franja-info__item">
            <h3>Horario de atención</h3>
            <p>
              Lunes a viernes: 5:00 a.m. – 9:00 p.m.
              <br />
              Sábados: 7:00 a.m. – 2:00 p.m.
            </p>
          </article>

          <article className="tarjeta franja-info__item">
            <h3>¿Por qué elegirnos?</h3>
            <p>Instructores certificados, equipo moderno y planes a la medida de cada cliente.</p>
          </article>

          <article className="tarjeta franja-info__item">
            <h3>¿Listo para empezar?</h3>
            <p>Inscribite en minutos y elegí la rutina o el curso que mejor se adapte a vos.</p>
            <Link to="/servicios/inscripcion" className="boton">Inscribirme</Link>
          </article>
        </div>
      </section>
    </>
  );
}
