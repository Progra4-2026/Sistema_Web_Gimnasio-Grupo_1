import { NavLink } from 'react-router-dom';
import './Submenu.css';

/**
 * Menú secundario reutilizable. Cada opción es una ruta hija (React Router), así que
 * cambiar de pestaña no recarga la página. Se usa en Servicios y en Mantenimiento.
 *
 * Props:
 *  - opciones: [{ ruta: 'inscripcion', etiqueta: 'Inscripción' }, ...]  (rutas relativas al padre)
 *  - etiquetaAria: texto para lectores de pantalla
 *  - onSeleccionar (opcional): avisa al componente padre qué opción se eligió
 */
export default function Submenu({ opciones, etiquetaAria, onSeleccionar }) {
  return (
    <nav className="submenu" aria-label={etiquetaAria}>
      <ul className="contenedor">
        {opciones.map((opcion) => (
          <li key={opcion.ruta}>
            <NavLink
              to={opcion.ruta}
              className={({ isActive }) => (isActive ? 'activo' : undefined)}
              onClick={() => onSeleccionar?.(opcion.ruta)}
            >
              {opcion.etiqueta}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
