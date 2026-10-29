import { Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Submenu from '../../components/menus/Submenu';
import '../mantenimiento/Mantenimiento.css';

const OPCIONES = [
  { ruta: 'rutinas', etiqueta: 'Rutinas' },
  { ruta: 'maquinas', etiqueta: 'Máquinas' },
];

/**
 * Vista privada /instructor: solo para el rol instructor (RutaPrivada en App.jsx y
 * @PreAuthorize en el backend). El instructor gestiona las máquinas y las rutinas de los clientes.
 */
export default function InstructorLayout() {
  const { usuario } = useAuth();

  return (
    <>
      <div className="titulo-seccion contenedor">
        <h1>Panel del instructor</h1>
        <p>Sesión iniciada como <strong>{usuario.nombre}</strong>. Asigná rutinas a los clientes y mantené el inventario de máquinas.</p>
      </div>

      <Submenu opciones={OPCIONES} etiquetaAria="Submenú del instructor" />

      <div className="contenedor">
        <Outlet />
      </div>
    </>
  );
}
