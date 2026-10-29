import { useCallback, useState } from 'react';
import { useNotificacion } from '../../context/NotificationContext';
import useRecursos from '../../hooks/useRecursos';
import Coleccion from '../../models/Coleccion';
import { listarMaquinas } from '../../services/maquinas';
import { actualizarRutina, crearRutina, eliminarRutina, listarRutinas } from '../../services/rutinas';
import Modal from '../../components/common/Modal';
import ConfirmacionModal from '../../components/common/ConfirmacionModal';
import RutinaForm from '../../components/forms/RutinaForm';
import TablaRutinas from '../../components/lists/TablaRutinas';

/** Ruta /instructor/rutinas: asignar, editar y eliminar las rutinas de los clientes (solo instructor). */
export default function InstructorRutinasPage() {
  const cargarRutinas = useCallback(() => listarRutinas(), []);
  const cargarMaquinas = useCallback(() => listarMaquinas(), []);
  const { elementos, cargando, error, recargar } = useRecursos(cargarRutinas);
  const { elementos: maquinas } = useRecursos(cargarMaquinas);
  const { notificar } = useNotificacion();
  const [filtro, setFiltro] = useState('');
  const [edicion, setEdicion] = useState(null);
  const [aEliminar, setAEliminar] = useState(null);
  const [eliminando, setEliminando] = useState(false);

  // Iterator: Coleccion.filtrar recorre las rutinas con for...of
  const texto = filtro.trim().toLowerCase();
  const visibles = new Coleccion(elementos).filtrar((r) =>
    !texto || String(r.cliente).includes(texto) || r.clienteNombre.toLowerCase().includes(texto));

  async function guardar(datos) {
    try {
      if (edicion.rutina) {
        const respuesta = await actualizarRutina(edicion.rutina.id, datos);
        notificar('exito', `${respuesta.mensaje} Registros afectados: ${respuesta.registrosAfectados}.`);
      } else {
        const creada = await crearRutina(datos);
        notificar('exito', `Rutina #${creada.id} asignada a ${creada.clienteNombre}.`);
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
      await eliminarRutina(aEliminar.id);
      notificar('exito', `Rutina #${aEliminar.id} eliminada. Registros eliminados: 1.`);
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
        <h2>Rutinas de los clientes</h2>
        <button type="button" className="boton" onClick={() => setEdicion({ rutina: null })}>+ Asignar rutina</button>
      </div>

      <div className="campo-formulario filtro-tabla">
        <label htmlFor="filtro-rutinas">Buscar por cliente</label>
        <input id="filtro-rutinas" value={filtro} onChange={(e) => setFiltro(e.target.value)} placeholder="Cédula o nombre" />
      </div>

      {cargando && <p>Cargando rutinas...</p>}
      {error && <p className="texto-error">{error}</p>}
      {!cargando && !error && (
        <TablaRutinas rutinas={[...visibles]} onEditar={(rutina) => setEdicion({ rutina })} onEliminar={setAEliminar} />
      )}

      <Modal abierto={edicion !== null} titulo={edicion?.rutina ? 'Editar rutina' : 'Asignar rutina'} onCerrar={() => setEdicion(null)}>
        {edicion && (
          <RutinaForm
            key={edicion.rutina?.id ?? 'nueva'}
            rutina={edicion.rutina}
            maquinas={maquinas}
            onGuardar={guardar}
            onCancelar={() => setEdicion(null)}
          />
        )}
      </Modal>

      <ConfirmacionModal
        abierto={aEliminar !== null}
        titulo="Eliminar rutina"
        mensaje={`¿Eliminar la rutina #${aEliminar?.id} de ${aEliminar?.clienteNombre}?`}
        procesando={eliminando}
        onConfirmar={confirmarEliminacion}
        onCancelar={() => setAEliminar(null)}
      />
    </section>
  );
}
