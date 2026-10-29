import { useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import useRecursos from '../../hooks/useRecursos';
import { listarMisRutinas } from '../../services/rutinas';
import LoginForm from '../../components/forms/LoginForm';
import RutinaTabla from '../../components/lists/RutinaTabla';

/** Rutinas del cliente con sesión iniciada. El backend solo devuelve las de su propia cédula. */
function MisRutinas({ usuario }) {
  const cargar = useCallback(() => listarMisRutinas(), []);
  const { elementos, cargando, error } = useRecursos(cargar);

  if (cargando) return <p>Cargando tus rutinas...</p>;
  if (error) return <p className="texto-error">{error}</p>;
  return <RutinaTabla nombreCliente={usuario.nombre} rutinas={elementos} />;
}

/** Ruta /servicios/rutinas: cada cliente ve solo sus rutinas, por eso requiere sesión de cliente. */
export default function RutinasPage() {
  const { usuario } = useAuth();

  let contenido;
  if (!usuario) {
    contenido = (
      <>
        <p><strong>Acceso restringido.</strong> Iniciá sesión con tu cédula y contraseña de cliente para ver tus rutinas asignadas.</p>
        <LoginForm />
      </>
    );
  } else if (usuario.esCliente) {
    contenido = <MisRutinas usuario={usuario} />;
  } else if (usuario.esInstructor) {
    contenido = (
      <p>
        Las rutinas de los clientes se administran desde el <Link to="/instructor/rutinas">Panel del instructor</Link>.
      </p>
    );
  } else {
    contenido = <p>Esta vista es para clientes: cada cliente ve únicamente sus propias rutinas.</p>;
  }

  return (
    <section className="seccion seccion-servicio">
      <h2>Rutinas</h2>
      <p>Las rutinas son personales: cada cliente ve únicamente las suyas una vez inscrito e iniciada la sesión.</p>
      <div className="tarjeta aviso-acceso">{contenido}</div>
    </section>
  );
}
