import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { listarCursos } from '../services/cursos';
import { useAuth } from '../context/AuthContext';
import InscripcionForm from '../components/forms/InscripcionForm';
import LoginForm from '../components/forms/LoginForm';
import DesinscripcionForm from '../components/forms/DesinscripcionForm';
import CursosList from '../components/lists/CursosList';
import MaquinasList from '../components/lists/MaquinasList';
import InstructoresList from '../components/lists/InstructoresList';
import RutinaTabla from '../components/lists/RutinaTabla';
import './ServiciosPage.css';

const SUBMENU = [
  { id: 'inscripcion', etiqueta: 'Inscripción' },
  { id: 'cursos', etiqueta: 'Cursos' },
  { id: 'rutinas', etiqueta: 'Rutinas' },
  { id: 'maquinas', etiqueta: 'Máquinas' },
  { id: 'instructores', etiqueta: 'Instructores' },
  { id: 'desinscribirse', etiqueta: 'Desinscribirse' },
];

export default function ServiciosPage() {
  const [cursos, setCursos] = useState([]);
  const [cargandoCursos, setCargandoCursos] = useState(true);
  const [errorCursos, setErrorCursos] = useState('');
  const [cursoSeleccionadoId, setCursoSeleccionadoId] = useState(null);

  const secciones = useRef({});
  const location = useLocation();
  const { usuario } = useAuth();

  useEffect(() => {
    listarCursos()
      .then(setCursos)
      .catch((error) => setErrorCursos(error.message))
      .finally(() => setCargandoCursos(false));
  }, []);

  useEffect(() => {
    if (!location.hash) return;
    const id = location.hash.replace('#', '');
    secciones.current[id]?.scrollIntoView({ behavior: 'smooth' });
  }, [location.hash]);

  function manejarInscribirme(curso) {
    setCursoSeleccionadoId(curso.id);
    secciones.current.inscripcion?.scrollIntoView({ behavior: 'smooth' });
  }

  return (
    <>
      <div className="titulo-seccion contenedor">
        <h1>Servicios</h1>
        <p>Todo lo que necesitás para tu proceso en el gimnasio, en un solo lugar.</p>
      </div>

      <nav className="submenu-servicios" aria-label="Submenú de servicios">
        <ul className="contenedor">
          {SUBMENU.map((item) => (
            <li key={item.id}>
              <a
                href={`#${item.id}`}
                onClick={(evento) => {
                  evento.preventDefault();
                  secciones.current[item.id]?.scrollIntoView({ behavior: 'smooth' });
                }}
              >
                {item.etiqueta}
              </a>
            </li>
          ))}
        </ul>
      </nav>

      <main className="contenedor">
        <section id="inscripcion" ref={(el) => (secciones.current.inscripcion = el)} className="seccion seccion-servicio">
          <h2>Inscripción</h2>
          <p>Completá tus datos para inscribirte a una rutina o a un curso.</p>
          <InscripcionForm
            cursos={cursos}
            cursoSeleccionadoId={cursoSeleccionadoId}
            onInscripcionExitosa={() => setCursoSeleccionadoId(null)}
          />
        </section>

        <section id="cursos" ref={(el) => (secciones.current.cursos = el)} className="seccion seccion-servicio">
          <h2>Cursos</h2>
          <p>Elegí el curso grupal que más se adapte a vos.</p>
          <CursosList cursos={cursos} cargando={cargandoCursos} error={errorCursos} onInscribirme={manejarInscribirme} />
        </section>

        <section id="rutinas" ref={(el) => (secciones.current.rutinas = el)} className="seccion seccion-servicio">
          <h2>Rutinas</h2>
          <p>Las rutinas son personales: cada cliente ve únicamente las suyas una vez inscrito e iniciada la sesión.</p>

          {usuario ? (
            <div className="tarjeta aviso-acceso">
              <RutinaTabla nombreCliente={usuario.nombre} />
            </div>
          ) : (
            <div className="tarjeta aviso-acceso">
              <p><strong>Acceso restringido.</strong> Iniciá sesión con tu cédula y contraseña de cliente para ver tus rutinas asignadas.</p>
              <LoginForm />
            </div>
          )}
        </section>

        <section id="maquinas" ref={(el) => (secciones.current.maquinas = el)} className="seccion seccion-servicio">
          <h2>Máquinas</h2>
          <p>Listado de máquinas y equipo disponible en nuestras instalaciones.</p>
          <MaquinasList />
        </section>

        <section id="instructores" ref={(el) => (secciones.current.instructores = el)} className="seccion seccion-servicio">
          <h2>Instructores</h2>
          <p>Conocé a nuestro equipo de instructores certificados.</p>
          <InstructoresList />
        </section>

        <section id="desinscribirse" ref={(el) => (secciones.current.desinscribirse = el)} className="seccion seccion-servicio">
          <h2>Desinscribirse</h2>
          <p>Podés retirar tu inscripción del gimnasio siempre y cuando te encontrés al día con tus pagos.</p>
          <DesinscripcionForm />
        </section>
      </main>
    </>
  );
}
