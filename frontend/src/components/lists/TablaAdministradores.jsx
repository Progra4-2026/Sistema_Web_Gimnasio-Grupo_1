import Coleccion from '../../models/Coleccion';

/** Tabla de administradores. No se ofrece eliminar la propia cuenta (el backend también lo impide). */
export default function TablaAdministradores({ administradores, codigoEnSesion, onEliminar }) {
  const coleccion = new Coleccion(administradores);
  if (coleccion.estaVacia()) return <p>No hay administradores registrados.</p>;
  const unico = coleccion.tamano === 1;

  return (
    <div className="tabla-contenedor">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">Código</th>
            <th scope="col">Nombre</th>
            <th scope="col">Correo</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {coleccion.mapear((admin) => {
            const esPropio = String(admin.codAdministrador) === String(codigoEnSesion);
            return (
              <tr key={admin.codAdministrador}>
                <td>{admin.codAdministrador}</td>
                <td>{admin.nombre} {esPropio && <span className="texto-ayuda">(tu cuenta)</span>}</td>
                <td>{admin.email || '—'}</td>
                <td>
                  <button
                    type="button"
                    className="boton boton-pequeno boton-peligro"
                    onClick={() => onEliminar(admin)}
                    disabled={esPropio || unico}
                    title={esPropio ? 'No podés eliminar tu propia cuenta' : unico ? 'Debe quedar al menos un administrador' : undefined}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
