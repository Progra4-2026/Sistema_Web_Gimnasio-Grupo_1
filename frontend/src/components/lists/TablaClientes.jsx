import Coleccion from '../../models/Coleccion';

/** Tabla de clientes inscritos para Mantenimiento. Los botones avisan al padre con onEditar / onEliminar. */
export default function TablaClientes({ clientes, onEditar, onEliminar }) {
  const coleccion = new Coleccion(clientes);
  if (coleccion.estaVacia()) return <p>No hay clientes inscritos todavía.</p>;

  return (
    <div className="tabla-contenedor">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">Cédula</th>
            <th scope="col">Nombre</th>
            <th scope="col">Correo</th>
            <th scope="col">Teléfono</th>
            <th scope="col">Edad</th>
            <th scope="col">Inscripción</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {coleccion.mapear((cliente) => (
            <tr key={cliente.cedula}>
              <td>{cliente.cedula}</td>
              <td>{cliente.nombre}</td>
              <td>{cliente.email}</td>
              <td>{cliente.telefono}</td>
              <td>{cliente.edad}</td>
              <td>{cliente.esACurso() ? `Curso #${cliente.idCurso}` : `Rutina (${cliente.objetivo})`}</td>
              <td>
                <div className="tabla-acciones">
                  <button type="button" className="boton boton-pequeno boton-secundario" onClick={() => onEditar(cliente)}>Editar</button>
                  <button type="button" className="boton boton-pequeno boton-peligro" onClick={() => onEliminar(cliente)}>Eliminar</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
