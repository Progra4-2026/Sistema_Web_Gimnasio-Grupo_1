import { useCallback, useState } from 'react';
import { useNotificacion } from '../../context/NotificationContext';
import useRecursos from '../../hooks/useRecursos';
import { actualizarInstructor, crearInstructor, eliminarInstructor, listarInstructores } from '../../services/admin';
import Modal from '../../components/common/Modal';
import ConfirmacionModal from '../../components/common/ConfirmacionModal';
import InstructorForm from '../../components/forms/InstructorForm';
import TablaInstructores from '../../components/lists/TablaInstructores';

/** Ruta /mantenimiento/instructores: CRUD de instructores (solo administrador). */
export default function MantenimientoInstructoresPage() {
  const cargar = useCallback(() => listarInstructores(), []);
  const { elementos, cargando, error, recargar } = useRecursos(cargar);
  const { notificar } = useNotificacion();

  // null = cerrado, { instructor: null } = crear, { instructor } = editar
  const [edicion, setEdicion] = useState(null);
  const [aEliminar, setAEliminar] = useState(null);
  const [eliminando, setEliminando] = useState(false);

  async function guardar(datos, password) {
    try {
      if (edicion.instructor) {
        const respuesta = await actualizarInstructor(edicion.instructor.codInstructor, datos, password);
        notificar('exito', `${respuesta.mensaje} Registros afectados: ${respuesta.registrosAfectados}.`);
      } else {
        const creado = await crearInstructor(datos, password);
        notificar('exito', `Instructor ${creado.nombreCompleto} creado. Su usuario para iniciar sesión es ${creado.codInstructor}.`);
      }
      setEdicion(null);
      recargar();
    } catch (err) {
      notificar('error', err.message);
      throw err;
    }
  }

  async function confirmarEliminacion() {
    setEliminando(true);
    try {
      await eliminarInstructor(aEliminar.codInstructor);
      notificar('exito', `Instructor ${aEliminar.nombreCompleto} eliminado. Registros eliminados: 1.`);
      recargar();
    } catch (err) {
      notificar('error', err.message); // 409: tiene rutinas o historial
    } finally {
      setEliminando(false);
      setAEliminar(null);
    }
  }

  return (
    <section className="seccion">
      <div className="barra-acciones">
        <h2>Instructores</h2>
        <button type="button" className="boton" onClick={() => setEdicion({ instructor: null })}>+ Nuevo instructor</button>
      </div>

      {cargando && <p>Cargando instructores...</p>}
      {error && <p className="texto-error">{error}</p>}
      {!cargando && !error && (
        <TablaInstructores instructores={elementos} onEditar={(instructor) => setEdicion({ instructor })} onEliminar={setAEliminar} />
      )}

      <Modal abierto={edicion !== null} titulo={edicion?.instructor ? 'Editar instructor' : 'Nuevo instructor'} onCerrar={() => setEdicion(null)}>
        {edicion && (
          <InstructorForm
            key={edicion.instructor?.codInstructor ?? 'nuevo'}
            instructor={edicion.instructor}
            onGuardar={guardar}
            onCancelar={() => setEdicion(null)}
          />
        )}
      </Modal>

      <ConfirmacionModal
        abierto={aEliminar !== null}
        titulo="Eliminar instructor"
        mensaje={`¿Eliminar al instructor ${aEliminar?.nombreCompleto} (código ${aEliminar?.codInstructor})? Si tiene rutinas o cursos registrados no se podrá eliminar.`}
        procesando={eliminando}
        onConfirmar={confirmarEliminacion}
        onCancelar={() => setAEliminar(null)}
      />
    </section>
  );
}
