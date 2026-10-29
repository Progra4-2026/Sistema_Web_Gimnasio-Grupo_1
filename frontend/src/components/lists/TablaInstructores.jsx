import Coleccion from '../../models/Coleccion';

/** Tabla de instructores (Mantenimiento del administrador). */
export default function TablaInstructores({ instructores, onEditar, onEliminar }) {
  const coleccion = new Coleccion(instructores);
  if (coleccion.estaVacia()) return <p>No hay instructores registrados.</p>;

  return (
    <div className="tabla-contenedor">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">Código</th>
            <th scope="col">Nombre</th>
            <th scope="col">Correo</th>
            <th scope="col">Celular</th>
            <th scope="col">Contratación</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {coleccion.mapear((instructor) => (
            <tr key={instructor.codInstructor}>
              <td>{instructor.codInstructor}</td>
              <td>{instructor.nombreCompleto}</td>
              <td>{instructor.email || '—'}</td>
              <td>{instructor.telCel}</td>
              <td>{instructor.fechaContratacion}</td>
              <td>
                <div className="tabla-acciones">
                  <button type="button" className="boton boton-pequeno boton-secundario" onClick={() => onEditar(instructor)}>Editar</button>
                  <button type="button" className="boton boton-pequeno boton-peligro" onClick={() => onEliminar(instructor)}>Eliminar</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
