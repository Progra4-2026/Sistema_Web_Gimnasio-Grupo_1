import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoginForm from '../components/forms/LoginForm';
import './Pages.css';

/**
 * Página de inicio de sesión. Si RutaPrivada mandó al usuario aquí,
 * al iniciar sesión vuelve a la página que quería abrir.
 */
export default function LoginPage() {
  const { usuario, cargando } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const destino = location.state?.desde || '/mantenimiento';

  if (!cargando && usuario) {
    return <Navigate to={destino} replace />;
  }

  return (
    <section className="seccion contenedor">
      <div className="titulo-seccion">
        <h1>Iniciar sesión</h1>
        <p>Ingresá con tu cédula y la contraseña de tu inscripción.</p>
      </div>
      <LoginForm onExito={() => navigate(destino, { replace: true })} />
    </section>
  );
}
