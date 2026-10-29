import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoginForm from '../components/forms/LoginForm';
import './Pages.css';

/**
 * Página de inicio de sesión (cliente, instructor o administrador).
 * Si RutaPrivada mandó al usuario aquí, vuelve a esa página (si su rol puede verla); si no, va a la sección de su rol:
 * cliente → Mis rutinas, instructor → Panel del instructor, administrador → Mantenimiento.
 */
export default function LoginPage() {
  const { usuario, cargando } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const desde = location.state?.desde;

  if (!cargando && usuario) {
    return <Navigate to={usuario.destinoTrasLogin(desde)} replace />;
  }

  return (
    <section className="seccion contenedor">
      <div className="titulo-seccion">
        <h1>Iniciar sesión</h1>
        <p>
          Clientes: ingresá con tu cédula. Instructores y administradores: con su código.
        </p>
      </div>
      <div className="tarjeta tarjeta-login">
        <LoginForm apilado onExito={(u) => navigate(u.destinoTrasLogin(desde), { replace: true })} />
      </div>
    </section>
  );
}
