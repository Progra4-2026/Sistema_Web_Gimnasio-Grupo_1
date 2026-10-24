import { useCallback, useEffect, useReducer, useState } from 'react';
import { useCursos } from '../../context/CursosContext';
import { useNotificacion } from '../../context/NotificationContext';
import { actualizarInscripcion, eliminarInscripcion, listarInscripciones } from '../../services/inscripciones';
import Modal from '../../components/common/Modal';
import ConfirmacionModal from '../../components/common/ConfirmacionModal';
import ClienteForm from '../../components/forms/ClienteForm';
import TablaClientes from '../../components/lists/TablaClientes';

// Estado de la lista de clientes con useReducer
const estadoInicial = { clientes: [], cargando: true, error: '' };

function reducer(estado, accion) {
  switch (accion.type) {
    case 'CARGA_INICIADA':
      return { ...estado, cargando: true, error: '' };
    case 'CARGA_EXITOSA':
      return { clientes: accion.payload, cargando: false, error: '' };
    case 'CARGA_FALLIDA':
      return { ...estado, cargando: false, error: accion.payload };
    case 'CLIENTE_ACTUALIZADO':
      return { ...estado, clientes: estado.clientes.map((c) => (c.cedula === accion.payload.cedula ? accion.payload : c)) };
    case 'CLIENTE_ELIMINADO':
      return { ...estado, clientes: estado.clientes.filter((c) => c.cedula !== accion.payload) };
    default:
      return estado;
  }
}

/**
 * Ruta /mantenimiento/clientes: listar (GET), editar datos de contacto (PUT) y eliminar (DELETE)
 * a los clientes inscritos.
 */
export default function MantenimientoClientesPage() {
  const [estado, dispatch] = useReducer(reducer, estadoInicial);
  const [aEditar, setAEditar] = useState(null);
  const [aEliminar, setAEliminar] = useState(null);
  const [eliminando, setEliminando] = useState(false);
  const { recargarCursos } = useCursos();
  const { notificar } = useNotificacion();

  const cargarClientes = useCallback(async () => {
    dispatch({ type: 'CARGA_INICIADA' });
    try {
      dispatch({ type: 'CARGA_EXITOSA', payload: await listarInscripciones() });
    } catch (err) {
      dispatch({ type: 'CARGA_FALLIDA', payload: err.message });
    }
  }, []);

  useEffect(() => {
    cargarClientes();
  }, [cargarClientes]);

  async function guardarCliente(datos) {
    try {
      const respuesta = await actualizarInscripcion(aEditar.cedula, datos);
      dispatch({ type: 'CLIENTE_ACTUALIZADO', payload: respuesta.dato });
      notificar('exito', `${respuesta.mensaje} Registros afectados: ${respuesta.registrosAfectados}.`);
      setAEditar(null);
    } catch (err) {
      notificar('error', err.message);
      throw err; // el formulario muestra los errores por campo
    }
  }

  async function confirmarEliminacion() {
    setEliminando(true);
    try {
      await eliminarInscripcion(aEliminar.cedula);
      dispatch({ type: 'CLIENTE_ELIMINADO', payload: aEliminar.cedula });
      notificar('exito', `Cliente ${aEliminar.nombre} eliminado. Registros eliminados: 1.`);
      recargarCursos(); // si estaba en un curso, se liberó un cupo
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
        <h2>Clientes inscritos</h2>
        <button type="button" className="boton boton-secundario" onClick={cargarClientes}>Actualizar lista</button>
      </div>

      {estado.cargando && <p>Cargando clientes...</p>}
      {estado.error && <p className="texto-error">{estado.error}</p>}
      {!estado.cargando && !estado.error && (
        <TablaClientes clientes={estado.clientes} onEditar={setAEditar} onEliminar={setAEliminar} />
      )}

      <Modal abierto={aEditar !== null} titulo="Editar cliente" onCerrar={() => setAEditar(null)}>
        {aEditar && (
          <ClienteForm key={aEditar.cedula} cliente={aEditar} onGuardar={guardarCliente} onCancelar={() => setAEditar(null)} />
        )}
      </Modal>

      <ConfirmacionModal
        abierto={aEliminar !== null}
        titulo="Eliminar cliente"
        mensaje={`¿Eliminar la inscripción de ${aEliminar?.nombre} (cédula ${aEliminar?.cedula})? Si estaba en un curso, se libera su cupo.`}
        procesando={eliminando}
        onConfirmar={confirmarEliminacion}
        onCancelar={() => setAEliminar(null)}
      />
    </section>
  );
}
