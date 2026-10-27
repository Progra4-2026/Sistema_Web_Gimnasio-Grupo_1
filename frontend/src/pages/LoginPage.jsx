import { useNavigate } from 'react-router-dom';
import LoginForm from '../components/forms/LoginForm';
import './Pages.css';

const RUTA_POR_ROL = {
  instructor: '/instructor',
  administrador: '/admin',
  cliente: '/servicios#rutinas',
};

export default function LoginPage() {
  const navigate = useNavigate();

  function manejarExito(usuario) {
    navigate(RUTA_POR_ROL[usuario?.rol] || '/');
  }

  return (
    <div className="contenedor seccion">
      <div className="titulo-seccion">
        <h1>Iniciar sesión</h1>
        <p>Cliente, instructor o administrador: usá tu cédula o código y tu contraseña.</p>
      </div>
      <div className="tarjeta aviso-acceso">
        <LoginForm onExito={manejarExito} />
      </div>
    </div>
  );
}
