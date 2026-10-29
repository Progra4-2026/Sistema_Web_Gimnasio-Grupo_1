import Coleccion from '../../models/Coleccion';

/** Tabla de rutinas del Panel del instructor (todas las rutinas del gimnasio). */
export default function TablaRutinas({ rutinas, onEditar, onEliminar }) {
  const coleccion = new Coleccion(rutinas);
  if (coleccion.estaVacia()) return <p>No hay rutinas que coincidan.</p>;

  return (
    <div className="tabla-contenedor">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">Cliente</th>
            <th scope="col">Máquina</th>
            <th scope="col">Instructor</th>
            <th scope="col">Fecha</th>
            <th scope="col">Horas</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {coleccion.mapear((rutina) => (
            <tr key={rutina.id}>
              <td>{rutina.id}</td>
              <td>{rutina.clienteNombre} <span className="texto-ayuda">({rutina.cliente})</span></td>
              <td>{rutina.maquinaDescripcion}</td>
              <td>{rutina.instructorNombre}</td>
              <td>{rutina.fechaLegible}</td>
              <td>{rutina.horas}</td>
              <td>
                <div className="tabla-acciones">
                  <button type="button" className="boton boton-pequeno boton-secundario" onClick={() => onEditar(rutina)}>Editar</button>
                  <button type="button" className="boton boton-pequeno boton-peligro" onClick={() => onEliminar(rutina)}>Eliminar</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
