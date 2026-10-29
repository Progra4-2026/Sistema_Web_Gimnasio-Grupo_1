import { useAuth } from '../../context/AuthContext';
import { useCursos } from '../../context/CursosContext';
import { useNotificacion } from '../../context/NotificationContext';
import DesinscripcionForm from '../../components/forms/DesinscripcionForm';
import LoginForm from '../../components/forms/LoginForm';

/**
 * Ruta /servicios/desinscribirse. Requiere sesión de cliente: cada cliente solo puede
 * eliminar su propia inscripción (el backend lo verifica con @PreAuthorize).
 */
export default function DesinscripcionPage() {
  const { usuario, cerrarSesion } = useAuth();
  const { recargarCursos } = useCursos();
  const { notificar } = useNotificacion();

  // Evento que sube desde el formulario: se libera el cupo del curso y se cierra la sesión (la cuenta ya no existe)
  async function despuesDeDesinscribirse() {
    recargarCursos();
    try {
      await cerrarSesion();
    } catch (err) {
      notificar('error', err.message);
    }
  }

  let contenido;
  if (!usuario) {
    contenido = (
      <div className="tarjeta aviso-acceso">
        <p><strong>Iniciá sesión</strong> con tu cédula y contraseña de cliente para retirar tu inscripción.</p>
        <LoginForm />
      </div>
    );
  } else if (usuario.esCliente) {
    contenido = <DesinscripcionForm cedula={usuario.cedula} onDesinscripcionExitosa={despuesDeDesinscribirse} />;
  } else {
    contenido = (
      <p className="tarjeta aviso-acceso">
        Esta opción es para clientes. {usuario.esAdministrador && 'Como administrador, podés eliminar clientes desde Mantenimiento → Clientes.'}
      </p>
    );
  }

  return (
    <section className="seccion seccion-servicio">
      <h2>Desinscribirse</h2>
      <p>Podés retirar tu inscripción del gimnasio siempre y cuando te encontrés al día con tus pagos.</p>
      {contenido}
    </section>
  );
}
