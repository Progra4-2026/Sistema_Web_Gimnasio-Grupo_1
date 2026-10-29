import { Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Submenu from '../../components/menus/Submenu';
import './Mantenimiento.css';

const OPCIONES = [
  { ruta: 'cursos', etiqueta: 'Cursos' },
  { ruta: 'clientes', etiqueta: 'Clientes' },
];

/**
 * Vista privada /mantenimiento: solo se llega con sesión iniciada (RutaPrivada en App.jsx).
 * Mismo Submenu reutilizable que en Servicios; cada pestaña es una ruta hija.
 */
export default function MantenimientoLayout() {
  const { usuario } = useAuth();

  return (
    <>
      <div className="titulo-seccion contenedor">
        <h1>Mantenimiento</h1>
        <p>Sesión iniciada como <strong>{usuario.nombre}</strong>. Administrá los cursos y los clientes inscritos.</p>
      </div>

      <Submenu opciones={OPCIONES} etiquetaAria="Submenú de mantenimiento" />

      <div className="contenedor">
        <Outlet />
      </div>
    </>
  );
}
