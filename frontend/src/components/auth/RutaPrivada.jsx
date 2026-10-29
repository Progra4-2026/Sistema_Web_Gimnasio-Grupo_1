import { Link, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

/**
 * AuthGuard: protege las rutas privadas.
 *  - Mientras se verifica la sesión con el backend, muestra un aviso.
 *  - Sin sesión, redirige a /login y recuerda a qué página se quería entrar.
 *  - Con sesión pero sin el rol requerido (prop roles), muestra "acceso denegado".
 *  - Con sesión y rol permitido, muestra la página (Outlet = la ruta hija).
 *
 * El backend vuelve a verificar el rol en cada petición (@PreAuthorize): esta guarda solo
 * evita mostrar pantallas que el usuario no puede usar.
 *
 * Uso en App.jsx:
 *   <Route element={<RutaPrivada roles={['administrador']} />}>
 *     <Route path="/mantenimiento" element={<MantenimientoLayout />} />
 *   </Route>
 */
export default function RutaPrivada({ roles }) {
  const { usuario, cargando } = useAuth();
  const location = useLocation();

  if (cargando) {
    return <p className="seccion contenedor">Verificando sesión...</p>;
  }

  if (!usuario) {
    return <Navigate to="/login" replace state={{ desde: location.pathname }} />;
  }

  if (roles && !usuario.tieneRol(...roles)) {
    return (
      <section className="seccion contenedor">
        <div className="tarjeta aviso-acceso">
          <h1>Acceso denegado</h1>
          <p>
            Tu cuenta de <strong>{usuario.rolLegible.toLowerCase()}</strong> no tiene permiso para ver esta sección.
          </p>
          <Link to={usuario.rutaInicio} className="boton">Ir a mi sección</Link>
        </div>
      </section>
    );
  }

  return <Outlet />;
}
