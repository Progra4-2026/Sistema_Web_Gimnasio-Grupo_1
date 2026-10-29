import Coleccion from '../../models/Coleccion';

/**
 * Los clientes de los datos de prueba (GIMNASIO.sql) no se inscribieron por la web:
 * no tienen tipo de inscripción ni fecha de nacimiento.
 */
function describirInscripcion(cliente) {
  if (cliente.esACurso()) return `Curso #${cliente.idCurso}`;
  if (cliente.tipoInscripcion === 'rutina') return `Rutina (${cliente.objetivo})`;
  return 'Registrado en el gimnasio';
}

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
              <td>{cliente.edad ?? '—'}</td>
              <td>{describirInscripcion(cliente)}</td>
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
