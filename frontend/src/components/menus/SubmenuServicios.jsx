import './SubmenuServicios.css';

/**
 * Menú secundario reutilizable (pestañas de Servicios).
 * Recibe las opciones y avisa al componente padre cuál se eligió mediante onSeleccionar,
 * sin recargar la página.
 */
export default function SubmenuServicios({ opciones, activa, onSeleccionar }) {
  return (
    <nav className="submenu-servicios" aria-label="Submenú de servicios">
      <ul className="contenedor">
        {opciones.map((opcion) => (
          <li key={opcion.id}>
            <a
              href={`#${opcion.id}`}
              className={activa === opcion.id ? 'activo' : undefined}
              aria-current={activa === opcion.id ? 'true' : undefined}
              onClick={(evento) => {
                evento.preventDefault();
                onSeleccionar(opcion.id);
              }}
            >
              {opcion.etiqueta}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
