import '../cards/Cards.css';
import Coleccion from '../../models/Coleccion';

/** Tabla de máquinas del Panel del instructor. Los botones avisan al padre con onEditar / onEliminar. */
export default function TablaMaquinas({ maquinas, onEditar, onEliminar }) {
  const coleccion = new Coleccion(maquinas);
  if (coleccion.estaVacia()) return <p>No hay máquinas registradas.</p>;

  return (
    <div className="tabla-contenedor">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">Descripción</th>
            <th scope="col">Estado</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {coleccion.mapear((maquina) => (
            <tr key={maquina.id}>
              <td>{maquina.id}</td>
              <td>{maquina.descripcion}</td>
              <td><span className={`etiqueta-estado estado-${maquina.estado.replace(' ', '-')}`}>{maquina.estado}</span></td>
              <td>
                <div className="tabla-acciones">
                  <button type="button" className="boton boton-pequeno boton-secundario" onClick={() => onEditar(maquina)}>Editar</button>
                  <button type="button" className="boton boton-pequeno boton-peligro" onClick={() => onEliminar(maquina)}>Eliminar</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
