import { useCallback, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNotificacion } from '../../context/NotificationContext';
import useRecursos from '../../hooks/useRecursos';
import { crearAdministrador, eliminarAdministrador, listarAdministradores } from '../../services/admin';
import Modal from '../../components/common/Modal';
import ConfirmacionModal from '../../components/common/ConfirmacionModal';
import AdministradorForm from '../../components/forms/AdministradorForm';
import TablaAdministradores from '../../components/lists/TablaAdministradores';

/** Ruta /mantenimiento/administradores: listar, crear y eliminar administradores. */
export default function MantenimientoAdministradoresPage() {
  const cargar = useCallback(() => listarAdministradores(), []);
  const { elementos, cargando, error, recargar } = useRecursos(cargar);
  const { usuario } = useAuth();
  const { notificar } = useNotificacion();
  const [creando, setCreando] = useState(false);
  const [aEliminar, setAEliminar] = useState(null);
  const [eliminando, setEliminando] = useState(false);

  async function guardar(datos, password) {
    try {
      const creado = await crearAdministrador(datos, password);
      notificar('exito', `Administrador ${creado.nombre} creado. Su usuario para iniciar sesión es ${creado.codAdministrador}.`);
      setCreando(false);
      recargar();
    } catch (err) {
      notificar('error', err.message);
      throw err;
    }
  }

  async function confirmarEliminacion() {
    setEliminando(true);
    try {
      await eliminarAdministrador(aEliminar.codAdministrador);
      notificar('exito', `Administrador ${aEliminar.nombre} eliminado. Registros eliminados: 1.`);
      recargar();
    } catch (err) {
      notificar('error', err.message);
    } finally {
      setEliminando(false);
      setAEliminar(null);
    }
  }

  return (
    <section className="seccion">
      <div className="barra-acciones">
        <h2>Administradores</h2>
        <button type="button" className="boton" onClick={() => setCreando(true)}>+ Nuevo administrador</button>
      </div>

      {cargando && <p>Cargando administradores...</p>}
      {error && <p className="texto-error">{error}</p>}
      {!cargando && !error && (
        <TablaAdministradores administradores={elementos} codigoEnSesion={usuario.cedula} onEliminar={setAEliminar} />
      )}

      <Modal abierto={creando} titulo="Nuevo administrador" onCerrar={() => setCreando(false)}>
        {creando && <AdministradorForm onGuardar={guardar} onCancelar={() => setCreando(false)} />}
      </Modal>

      <ConfirmacionModal
        abierto={aEliminar !== null}
        titulo="Eliminar administrador"
        mensaje={`¿Eliminar al administrador ${aEliminar?.nombre} (código ${aEliminar?.codAdministrador})?`}
        procesando={eliminando}
        onConfirmar={confirmarEliminacion}
        onCancelar={() => setAEliminar(null)}
      />
    </section>
  );
}
