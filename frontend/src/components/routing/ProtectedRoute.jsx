import { useEffect, useRef } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useNotificacion } from '../../context/NotificationContext';

export default function ProtectedRoute({ roles }) {
  const { usuario, cargando } = useAuth();
  const { notificar } = useNotificacion();
  const notificarRef = useRef(notificar);
  const location = useLocation();
  const sinPermiso = !cargando && usuario && roles && !roles.includes(usuario.rol);

  useEffect(() => {
    notificarRef.current = notificar;
  }, [notificar]);

  useEffect(() => {
    if (sinPermiso) notificarRef.current('error', 'No tenés permiso para acceder a esa sección.');
  }, [sinPermiso]);

  if (cargando) return null;
  if (!usuario) return <Navigate to="/login" state={{ from: location }} replace />;
  if (sinPermiso) return <Navigate to="/" replace />;
  return <Outlet />;
}
