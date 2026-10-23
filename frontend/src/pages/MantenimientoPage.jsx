import { useAuth } from '../context/AuthContext';
import './Pages.css';

/**
 * Vista privada: solo se llega aquí con sesión iniciada (ver RutaPrivada).
 * TEMPORAL: el contenido de Mantenimiento lo completa el enrutamiento del Entregable 5.
 */
export default function MantenimientoPage() {
  const { usuario } = useAuth();

  return (
    <section className="seccion contenedor">
      <div className="titulo-seccion">
        <h1>Mantenimiento</h1>
        <p>Sesión iniciada como {usuario.nombre}.</p>
      </div>
      <p>Esta sección solo es visible para usuarios autenticados.</p>
    </section>
  );
}
