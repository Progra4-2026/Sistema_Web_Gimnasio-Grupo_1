import { useAuth } from '../../context/AuthContext';
import LoginForm from '../../components/forms/LoginForm';
import RutinaTabla from '../../components/lists/RutinaTabla';

/** Ruta /servicios/rutinas: cada cliente ve solo su rutina, por eso requiere sesión. */
export default function RutinasPage() {
  const { usuario } = useAuth();

  return (
    <section className="seccion seccion-servicio">
      <h2>Rutinas</h2>
      <p>Las rutinas son personales: cada cliente ve únicamente las suyas una vez inscrito e iniciada la sesión.</p>

      {usuario ? (
        <div className="tarjeta aviso-acceso">
          <RutinaTabla nombreCliente={usuario.nombre} />
        </div>
      ) : (
        <div className="tarjeta aviso-acceso">
          <p><strong>Acceso restringido.</strong> Iniciá sesión con tu cédula y contraseña de cliente para ver tus rutinas asignadas.</p>
          <LoginForm />
        </div>
      )}
    </section>
  );
}
