import { useEffect, useState } from 'react';
import { listarMaquinas, eliminarMaquina } from '../../api/maquinas';
import { useNotificacion } from '../../context/NotificationContext';
import MaquinaCard from '../cards/MaquinaCard';
import Modal from '../common/Modal';
import MaquinaForm from '../forms/MaquinaForm';

/** soloLectura: usado en la página pública de Servicios. Si es false, agrega alta/edición/borrado. */
export default function MaquinasList({ soloLectura = true }) {
  const [maquinas, setMaquinas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [formAbierto, setFormAbierto] = useState(false);
  const [maquinaEditando, setMaquinaEditando] = useState(null);
  const { notificar } = useNotificacion();

  function cargar() {
    listarMaquinas().then(setMaquinas).catch((err) => setError(err.message)).finally(() => setCargando(false));
  }

  useEffect(() => {
    cargar();
  }, []);

  function abrirNueva() {
    setMaquinaEditando(null);
    setFormAbierto(true);
  }

  function abrirEdicion(maquina) {
    setMaquinaEditando(maquina);
    setFormAbierto(true);
  }

  async function manejarEliminar(id) {
    try {
      await eliminarMaquina(id);
      notificar('exito', 'Máquina eliminada correctamente.');
      cargar();
    } catch (err) {
      notificar('error', err.message);
    }
  }

  function manejarGuardado() {
    setFormAbierto(false);
    cargar();
  }

  if (cargando) return <p>Cargando máquinas...</p>;
  if (error) return <p className="texto-error">{error}</p>;

  return (
    <>
      {!soloLectura && (
        <button type="button" className="boton" onClick={abrirNueva}>Nueva máquina</button>
      )}

      <div className="rejilla rejilla--estrecha">
        {maquinas.map((maquina) => (
          <MaquinaCard
            key={maquina.id}
            maquina={maquina}
            onEditar={soloLectura ? undefined : abrirEdicion}
            onEliminar={soloLectura ? undefined : manejarEliminar}
          />
        ))}
      </div>

      {!soloLectura && (
        <Modal abierto={formAbierto} titulo={maquinaEditando ? 'Editar máquina' : 'Nueva máquina'}
               onCerrar={() => setFormAbierto(false)}>
          <MaquinaForm maquina={maquinaEditando} onGuardado={manejarGuardado} />
        </Modal>
      )}
    </>
  );
}
