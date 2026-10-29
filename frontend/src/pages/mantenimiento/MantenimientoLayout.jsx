import { Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Submenu from '../../components/menus/Submenu';
import './Mantenimiento.css';

const OPCIONES = [
  { ruta: 'cursos', etiqueta: 'Cursos' },
  { ruta: 'clientes', etiqueta: 'Clientes' },
  { ruta: 'instructores', etiqueta: 'Instructores' },
  { ruta: 'administradores', etiqueta: 'Administradores' },
  { ruta: 'parametros', etiqueta: 'Parámetros' },
];

/**
 * Vista privada /mantenimiento: solo para el rol administrador (RutaPrivada en App.jsx y
 * @PreAuthorize en el backend). Mismo Submenu reutilizable que en Servicios.
 */
export default function MantenimientoLayout() {
  const { usuario } = useAuth();

  return (
    <>
      <div className="titulo-seccion contenedor">
        <h1>Mantenimiento</h1>
        <p>
          Sesión iniciada como <strong>{usuario.nombre}</strong> (administrador). Administrá cursos, usuarios y
          parámetros del sistema.
        </p>
      </div>

      <Submenu opciones={OPCIONES} etiquetaAria="Submenú de mantenimiento" />

      <div className="contenedor">
        <Outlet />
      </div>
    </>
  );
}
