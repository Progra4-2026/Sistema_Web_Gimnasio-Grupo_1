import Coleccion from '../../models/Coleccion';

/** Tabla de cursos para Mantenimiento. Los botones avisan al padre con onEditar / onEliminar. */
export default function TablaCursos({ cursos, onEditar, onEliminar }) {
  const coleccion = new Coleccion(cursos);
  if (coleccion.estaVacia()) return <p>No hay cursos registrados.</p>;

  return (
    <div className="tabla-contenedor">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">Nombre</th>
            <th scope="col">Horario</th>
            <th scope="col">Cupos</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {coleccion.mapear((curso) => (
            <tr key={curso.id}>
              <td>{curso.id}</td>
              <td>{curso.descripcion}</td>
              <td>{curso.horario}</td>
              <td>{curso.cupos}</td>
              <td>
                <div className="tabla-acciones">
                  <button type="button" className="boton boton-pequeno boton-secundario" onClick={() => onEditar(curso)}>Editar</button>
                  <button type="button" className="boton boton-pequeno boton-peligro" onClick={() => onEliminar(curso)}>Eliminar</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
