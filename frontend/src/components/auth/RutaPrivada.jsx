import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

/**
 * AuthGuard: protege las rutas privadas (por ejemplo /mantenimiento).
 *  - Mientras se verifica la sesión con el backend, muestra un aviso.
 *  - Sin sesión, redirige a /login y recuerda a qué página se quería entrar.
 *  - Con sesión, muestra la página (Outlet = la ruta hija).
 *
 * Uso en App.jsx:
 *   <Route element={<RutaPrivada />}>
 *     <Route path="/mantenimiento" element={<MantenimientoPage />} />
 *   </Route>
 */
export default function RutaPrivada() {
  const { usuario, cargando } = useAuth();
  const location = useLocation();

  if (cargando) {
    return <p className="seccion contenedor">Verificando sesión...</p>;
  }

  if (!usuario) {
    return <Navigate to="/login" replace state={{ desde: location.pathname }} />;
  }

  return <Outlet />;
}
