import { useState } from 'react';
import { useCursos } from '../../context/CursosContext';
import { useNotificacion } from '../../context/NotificationContext';
import { actualizarCurso, crearCurso, eliminarCurso } from '../../services/cursos';
import Modal from '../../components/common/Modal';
import ConfirmacionModal from '../../components/common/ConfirmacionModal';
import CursoForm from '../../components/forms/CursoForm';
import TablaCursos from '../../components/lists/TablaCursos';

/**
 * Ruta /mantenimiento/cursos: listar (GET), crear (POST), editar (PUT) y eliminar (DELETE) cursos.
 * La lista viene del estado global CursosContext, así Servicios ve los cambios al instante.
 */
export default function MantenimientoCursosPage() {
  const { cursos, cargando, error, recargarCursos } = useCursos();
  const { notificar } = useNotificacion();

  // null = cerrado, { curso: null } = crear, { curso } = editar
  const [edicion, setEdicion] = useState(null);
  const [aEliminar, setAEliminar] = useState(null);
  const [eliminando, setEliminando] = useState(false);

  // Evento que sube desde CursoForm. Si falla, el error vuelve al formulario para mostrar los campos.
  async function guardarCurso(datos) {
    try {
      if (edicion.curso) {
        const respuesta = await actualizarCurso(edicion.curso.id, datos);
        notificar('exito', `${respuesta.mensaje} Registros afectados: ${respuesta.registrosAfectados}.`);
      } else {
        const creado = await crearCurso(datos);
        notificar('exito', `Curso "${creado.descripcion}" creado con el ID ${creado.id}.`);
      }
      setEdicion(null);
      recargarCursos();
    } catch (err) {
      notificar('error', err.message);
      throw err;
    }
  }

  async function confirmarEliminacion() {
    setEliminando(true);
    try {
      await eliminarCurso(aEliminar.id);
      notificar('exito', `Curso "${aEliminar.descripcion}" eliminado. Registros eliminados: 1.`);
      recargarCursos();
    } catch (err) {
      // 409: el curso tiene clientes inscritos; el backend explica la relación
      notificar('error', err.message);
    } finally {
      setEliminando(false);
      setAEliminar(null);
    }
  }

  return (
    <section className="seccion">
      <div className="barra-acciones">
        <h2>Cursos</h2>
        <button type="button" className="boton" onClick={() => setEdicion({ curso: null })}>+ Nuevo curso</button>
      </div>

      {cargando && <p>Cargando cursos...</p>}
      {error && <p className="texto-error">{error}</p>}
      {!cargando && !error && (
        <TablaCursos cursos={cursos} onEditar={(curso) => setEdicion({ curso })} onEliminar={setAEliminar} />
      )}

      <Modal abierto={edicion !== null} titulo={edicion?.curso ? 'Editar curso' : 'Nuevo curso'} onCerrar={() => setEdicion(null)}>
        {edicion && (
          <CursoForm
            key={edicion.curso?.id ?? 'nuevo'}
            curso={edicion.curso}
            onGuardar={guardarCurso}
            onCancelar={() => setEdicion(null)}
          />
        )}
      </Modal>

      <ConfirmacionModal
        abierto={aEliminar !== null}
        titulo="Eliminar curso"
        mensaje={`¿Eliminar el curso "${aEliminar?.descripcion}"? Esta acción no se puede deshacer.`}
        procesando={eliminando}
        onConfirmar={confirmarEliminacion}
        onCancelar={() => setAEliminar(null)}
      />
    </section>
  );
}
