import { useCallback, useState } from 'react';
import { useNotificacion } from '../../context/NotificationContext';
import useRecursos from '../../hooks/useRecursos';
import { actualizarParametro, listarParametros } from '../../services/admin';
import Modal from '../../components/common/Modal';
import ParametroForm from '../../components/forms/ParametroForm';
import TablaParametros from '../../components/lists/TablaParametros';

/** Ruta /mantenimiento/parametros: parámetros del sistema (solo administrador). */
export default function MantenimientoParametrosPage() {
  const cargar = useCallback(() => listarParametros(), []);
  const { elementos, cargando, error, recargar } = useRecursos(cargar);
  const { notificar } = useNotificacion();
  const [aEditar, setAEditar] = useState(null);

  async function guardar(valor) {
    try {
      const respuesta = await actualizarParametro(aEditar.clave, valor);
      notificar('exito', `${respuesta.mensaje} Registros afectados: ${respuesta.registrosAfectados}.`);
      setAEditar(null);
      recargar();
    } catch (err) {
      notificar('error', err.message);
      throw err;
    }
  }

  return (
    <section className="seccion">
      <div className="barra-acciones">
        <h2>Parámetros del sistema</h2>
        <button type="button" className="boton boton-secundario" onClick={recargar}>Actualizar lista</button>
      </div>

      {cargando && <p>Cargando parámetros...</p>}
      {error && <p className="texto-error">{error}</p>}
      {!cargando && !error && <TablaParametros parametros={elementos} onEditar={setAEditar} />}

      <Modal abierto={aEditar !== null} titulo="Editar parámetro" onCerrar={() => setAEditar(null)}>
        {aEditar && <ParametroForm key={aEditar.clave} parametro={aEditar} onGuardar={guardar} onCancelar={() => setAEditar(null)} />}
      </Modal>
    </section>
  );
}
