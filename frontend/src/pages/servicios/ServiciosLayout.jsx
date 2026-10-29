import { Outlet } from 'react-router-dom';
import Submenu from '../../components/menus/Submenu';
import './Servicios.css';

/** Rutas hijas de /servicios. Cada una es una vista propia (React Router), sin recargar la página. */
const OPCIONES_SERVICIOS = [
  { ruta: 'inscripcion', etiqueta: 'Inscripción' },
  { ruta: 'cursos', etiqueta: 'Cursos' },
  { ruta: 'rutinas', etiqueta: 'Rutinas' },
  { ruta: 'maquinas', etiqueta: 'Máquinas' },
  { ruta: 'instructores', etiqueta: 'Instructores' },
  { ruta: 'desinscribirse', etiqueta: 'Desinscribirse' },
];

/**
 * Plantilla de la sección Servicios: título + submenú + la vista hija elegida (Outlet).
 */
export default function ServiciosLayout() {
  return (
    <>
      <div className="titulo-seccion contenedor">
        <h1>Servicios</h1>
        <p>Todo lo que necesitás para tu proceso en el gimnasio, en un solo lugar.</p>
      </div>

      <Submenu opciones={OPCIONES_SERVICIOS} etiquetaAria="Submenú de servicios" />

      <div className="contenedor">
        <Outlet />
      </div>
    </>
  );
}
