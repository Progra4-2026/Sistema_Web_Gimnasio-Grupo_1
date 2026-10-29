import { useCallback, useState } from 'react';
import { useNotificacion } from '../../context/NotificationContext';
import useRecursos from '../../hooks/useRecursos';
import { actualizarMaquina, crearMaquina, eliminarMaquina, listarMaquinas } from '../../services/maquinas';
import Modal from '../../components/common/Modal';
import ConfirmacionModal from '../../components/common/ConfirmacionModal';
import MaquinaForm from '../../components/forms/MaquinaForm';
import TablaMaquinas from '../../components/lists/TablaMaquinas';

/** Ruta /instructor/maquinas: CRUD de máquinas (solo instructor). */
export default function InstructorMaquinasPage() {
  const cargar = useCallback(() => listarMaquinas(), []);
  const { elementos, cargando, error, recargar } = useRecursos(cargar);
  const { notificar } = useNotificacion();
  const [edicion, setEdicion] = useState(null);
  const [aEliminar, setAEliminar] = useState(null);
  const [eliminando, setEliminando] = useState(false);

  async function guardar(datos) {
    try {
      if (edicion.maquina) {
        const respuesta = await actualizarMaquina(edicion.maquina.id, datos);
        notificar('exito', `${respuesta.mensaje} Registros afectados: ${respuesta.registrosAfectados}.`);
      } else {
        const creada = await crearMaquina(datos);
        notificar('exito', `Máquina "${creada.descripcion}" creada con el ID ${creada.id}.`);
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
      await eliminarMaquina(aEliminar.id);
      notificar('exito', `Máquina "${aEliminar.descripcion}" eliminada. Registros eliminados: 1.`);
      recargar();
    } catch (err) {
      notificar('error', err.message); // 409: la máquina está en rutinas
    } finally {
      setEliminando(false);
      setAEliminar(null);
    }
  }

  return (
    <section className="seccion">
      <div className="barra-acciones">
        <h2>Máquinas</h2>
        <button type="button" className="boton" onClick={() => setEdicion({ maquina: null })}>+ Nueva máquina</button>
      </div>

      {cargando && <p>Cargando máquinas...</p>}
      {error && <p className="texto-error">{error}</p>}
      {!cargando && !error && (
        <TablaMaquinas maquinas={elementos} onEditar={(maquina) => setEdicion({ maquina })} onEliminar={setAEliminar} />
      )}

      <Modal abierto={edicion !== null} titulo={edicion?.maquina ? 'Editar máquina' : 'Nueva máquina'} onCerrar={() => setEdicion(null)}>
        {edicion && (
          <MaquinaForm key={edicion.maquina?.id ?? 'nueva'} maquina={edicion.maquina} onGuardar={guardar} onCancelar={() => setEdicion(null)} />
        )}
      </Modal>

      <ConfirmacionModal
        abierto={aEliminar !== null}
        titulo="Eliminar máquina"
        mensaje={`¿Eliminar la máquina "${aEliminar?.descripcion}"? Si está asignada en alguna rutina no se podrá eliminar.`}
        procesando={eliminando}
        onConfirmar={confirmarEliminacion}
        onCancelar={() => setAEliminar(null)}
      />
    </section>
  );
}
