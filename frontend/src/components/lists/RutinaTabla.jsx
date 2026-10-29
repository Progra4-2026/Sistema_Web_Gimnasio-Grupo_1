import Coleccion from '../../models/Coleccion';

/** Rutinas del cliente con sesión iniciada (GET /api/rutinas/mias): solo lectura. */
export default function RutinaTabla({ nombreCliente, rutinas }) {
  const coleccion = new Coleccion(rutinas);
  const totalHoras = coleccion.sumar((r) => r.horas);

  return (
    <>
      <h3>Rutinas asignadas a {nombreCliente}</h3>
      {coleccion.estaVacia() ? (
        <p>Todavía no tenés rutinas asignadas. Tu instructor te las asignará pronto.</p>
      ) : (
        <div className="tabla-contenedor">
          <table className="tabla-datos">
            <thead>
              <tr>
                <th scope="col">Fecha</th>
                <th scope="col">Máquina</th>
                <th scope="col">Horas</th>
                <th scope="col">Instructor</th>
              </tr>
            </thead>
            <tbody>
              {coleccion.mapear((rutina) => (
                <tr key={rutina.id}>
                  <td>{rutina.fechaLegible}</td>
                  <td>{rutina.maquinaDescripcion}</td>
                  <td>{rutina.horas}</td>
                  <td>{rutina.instructorNombre}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <th scope="row" colSpan={2}>Total</th>
                <td colSpan={2}>{totalHoras} h en {coleccion.tamano} rutina(s)</td>
              </tr>
            </tfoot>
          </table>
        </div>
      )}
    </>
  );
}
