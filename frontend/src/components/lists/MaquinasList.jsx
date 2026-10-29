import { useCallback } from 'react';
import MaquinaCard from '../cards/MaquinaCard';
import Coleccion from '../../models/Coleccion';
import useRecursos from '../../hooks/useRecursos';
import { listarMaquinas } from '../../services/maquinas';

/** Máquinas del gimnasio desde la base de datos (GET /api/maquinas, público). */
export default function MaquinasList() {
  const cargar = useCallback(() => listarMaquinas(), []);
  const { elementos, cargando, error } = useRecursos(cargar);

  if (cargando) return <p>Cargando máquinas...</p>;
  if (error) return <p className="texto-error">{error}</p>;

  const maquinas = new Coleccion(elementos);
  if (maquinas.estaVacia()) return <p>No hay máquinas registradas.</p>;

  return (
    <div className="rejilla rejilla--estrecha">
      {maquinas.mapear((maquina) => (
        <MaquinaCard key={maquina.id} maquina={maquina} />
      ))}
    </div>
  );
}
