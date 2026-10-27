import { useEffect, useState } from 'react';
import { listarRutinas, misRutinas, eliminarRutina } from '../../api/rutinas';
import { useNotificacion } from '../../context/NotificationContext';
import Modal from '../common/Modal';
import RutinaForm from '../forms/RutinaForm';

/**
 * modo "cliente": trae solo las rutinas del cliente en sesión, de solo lectura.
 * modo "instructor": trae todas las rutinas y agrega alta/edición/borrado.
 */
export default function RutinaTabla({ modo = 'cliente', nombreCliente }) {
  const [rutinas, setRutinas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [formAbierto, setFormAbierto] = useState(false);
  const [rutinaEditando, setRutinaEditando] = useState(null);
  const { notificar } = useNotificacion();

  function cargar() {
    const pedido = modo === 'instructor' ? listarRutinas() : misRutinas();
    pedido.then(setRutinas).catch((err) => setError(err.message)).finally(() => setCargando(false));
  }

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [modo]);

  function abrirNueva() {
    setRutinaEditando(null);
    setFormAbierto(true);
  }

  function abrirEdicion(rutina) {
    setRutinaEditando(rutina);
    setFormAbierto(true);
  }

  async function manejarEliminar(id) {
    try {
      await eliminarRutina(id);
      notificar('exito', 'Rutina eliminada correctamente.');
      cargar();
    } catch (err) {
      notificar('error', err.message);
    }
  }

  function manejarGuardado() {
    setFormAbierto(false);
    cargar();
  }

  if (cargando) return <p>Cargando rutinas...</p>;
  if (error) return <p className="texto-error">{error}</p>;

  return (
    <>
      {modo === 'cliente' && <h3>Rutina asignada a {nombreCliente}</h3>}
      {modo === 'instructor' && (
        <button type="button" className="boton" onClick={abrirNueva}>Nueva rutina</button>
      )}

      {rutinas.length === 0 ? (
        <p>No hay rutinas {modo === 'cliente' ? 'asignadas todavía.' : 'registradas.'}</p>
      ) : (
        <table className="tabla-datos">
          <thead>
            <tr>
              <th scope="col">Cliente</th>
              <th scope="col">Máquina</th>
              <th scope="col">Fecha</th>
              <th scope="col">Horas</th>
              {modo === 'instructor' && <th scope="col">Acciones</th>}
            </tr>
          </thead>
          <tbody>
            {rutinas.map((rutina) => (
              <tr key={rutina.id}>
                <td>{rutina.cliente}</td>
                <td>{rutina.maquina}</td>
                <td>{rutina.fecha}</td>
                <td>{rutina.horas}</td>
                {modo === 'instructor' && (
                  <td>
                    <button type="button" className="boton boton-secundario" onClick={() => abrirEdicion(rutina)}>
                      Editar
                    </button>{' '}
                    <button type="button" className="boton boton-peligro" onClick={() => manejarEliminar(rutina.id)}>
                      Eliminar
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {modo === 'instructor' && (
        <Modal abierto={formAbierto} titulo={rutinaEditando ? 'Editar rutina' : 'Nueva rutina'}
               onCerrar={() => setFormAbierto(false)}>
          <RutinaForm rutina={rutinaEditando} onGuardado={manejarGuardado} />
        </Modal>
      )}
    </>
  );
}
