import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useNotificacion } from '../../context/NotificationContext';
import './Navbar.css';

const ENLACES = [
  { ruta: '/', etiqueta: 'Inicio' },
  { ruta: '/contacto', etiqueta: 'Contactos' },
  { ruta: '/servicios', etiqueta: 'Servicios' },
  { ruta: '/blog', etiqueta: 'Blog' },
];

export default function Navbar() {
  const [menuAbierto, setMenuAbierto] = useState(false);
  const { usuario, cerrarSesion } = useAuth();
  const { notificar } = useNotificacion();

  function manejarClicEnlace() {
    setMenuAbierto(false);
  }

  async function manejarCerrarSesion() {
    await cerrarSesion();
    notificar('info', 'Sesión cerrada.');
    setMenuAbierto(false);
  }

  return (
    <header className="encabezado-sitio">
      <div className="barra-navegacion contenedor">
        <NavLink to="/" className="logo-gimnasio" onClick={manejarClicEnlace}>
          Gimnasio <span className="acento">PowerFit</span>
        </NavLink>

        <button
          type="button"
          className="boton-menu-movil"
          aria-expanded={menuAbierto}
          onClick={() => setMenuAbierto((anterior) => !anterior)}
        >
          Menú
        </button>

        <nav aria-label="Menú principal" className={menuAbierto ? 'menu-abierto' : ''}>
          <ul className="menu-principal">
            {ENLACES.map((enlace) => (
              <li key={enlace.ruta}>
                <NavLink to={enlace.ruta} onClick={manejarClicEnlace}>
                  {enlace.etiqueta}
                </NavLink>
              </li>
            ))}
          </ul>

          {!usuario && (
            <NavLink to="/login" className="boton boton-secundario" onClick={manejarClicEnlace}>
              Iniciar sesión
            </NavLink>
          )}

          {usuario && (
            <div className="sesion-navbar">
              <span>Hola, {usuario.nombre}</span>
              <button type="button" className="boton boton-secundario" onClick={manejarCerrarSesion}>
                Cerrar sesión
              </button>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
}
