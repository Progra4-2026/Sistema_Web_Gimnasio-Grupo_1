import Coleccion from '../../models/Coleccion';

/** Tabla de parámetros del sistema: solo se edita el valor. */
export default function TablaParametros({ parametros, onEditar }) {
  const coleccion = new Coleccion(parametros);
  if (coleccion.estaVacia()) return <p>No hay parámetros configurados.</p>;

  return (
    <div className="tabla-contenedor">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">Parámetro</th>
            <th scope="col">Descripción</th>
            <th scope="col">Valor</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {coleccion.mapear((parametro) => (
            <tr key={parametro.clave}>
              <td><code>{parametro.clave}</code></td>
              <td>{parametro.descripcion}</td>
              <td><strong>{parametro.valor}</strong></td>
              <td>
                <button type="button" className="boton boton-pequeno boton-secundario" onClick={() => onEditar(parametro)}>Editar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
