import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCursos } from '../context/CursosContext';
import SubmenuServicios from '../components/menus/SubmenuServicios';
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
  // Estado global (CursosContext) + estado local de la página (useState)
  const { cursos, cargando: cargandoCursos, error: errorCursos, recargarCursos } = useCursos();
  const [cursoSeleccionadoId, setCursoSeleccionadoId] = useState(null);
  const [seccionActiva, setSeccionActiva] = useState('inscripcion');

  const secciones = useRef({});
  const location = useLocation();
  const { usuario } = useAuth();

  function irASeccion(id) {
    setSeccionActiva(id);
    secciones.current[id]?.scrollIntoView({ behavior: 'smooth' });
  }

  // Marca en el submenú la sección que está a la vista mientras el usuario hace scroll
  useEffect(() => {
    const observador = new IntersectionObserver(
      (entradas) => {
        for (const entrada of entradas) {
          if (entrada.isIntersecting) setSeccionActiva(entrada.target.id);
        }
      },
      { rootMargin: '-35% 0px -60% 0px' },
    );
    for (const seccion of Object.values(secciones.current)) {
      if (seccion) observador.observe(seccion);
    }
    return () => observador.disconnect();
  }, []);

  useEffect(() => {
    if (!location.hash) return;
    const id = location.hash.replace('#', '');
    secciones.current[id]?.scrollIntoView({ behavior: 'smooth' });
  }, [location.hash]);

  // Evento que sube desde CursoCard → CursosList → esta página
  function manejarInscribirme(curso) {
    setCursoSeleccionadoId(curso.id);
    irASeccion('inscripcion');
  }

  // Eventos que suben desde los formularios: se recargan los cupos de los cursos
  function manejarInscripcionExitosa() {
    setCursoSeleccionadoId(null);
    recargarCursos();
  }

  return (
    <>
      <div className="titulo-seccion contenedor">
        <h1>Servicios</h1>
        <p>Todo lo que necesitás para tu proceso en el gimnasio, en un solo lugar.</p>
      </div>

      <SubmenuServicios opciones={SUBMENU} activa={seccionActiva} onSeleccionar={irASeccion} />

      <main className="contenedor">
        <section id="inscripcion" ref={(el) => (secciones.current.inscripcion = el)} className="seccion seccion-servicio">
          <h2>Inscripción</h2>
          <p>Completá tus datos para inscribirte a una rutina o a un curso.</p>
          <InscripcionForm
            cursos={cursos}
            cursoSeleccionadoId={cursoSeleccionadoId}
            onInscripcionExitosa={manejarInscripcionExitosa}
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
          <DesinscripcionForm onDesinscripcionExitosa={recargarCursos} />
        </section>
      </main>
    </>
  );
}
