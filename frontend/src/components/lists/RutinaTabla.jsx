const RUTINA_EJEMPLO = [
  { dia: 'Lunes', ejercicio: 'Sentadillas', series: '4 x 12' },
  { dia: 'Miércoles', ejercicio: 'Press de banca', series: '4 x 10' },
  { dia: 'Viernes', ejercicio: 'Peso muerto', series: '3 x 8' },
];

export default function RutinaTabla({ nombreCliente }) {
  return (
    <>
      <h3>Rutina asignada a {nombreCliente}</h3>
      <table className="tabla-datos">
        <thead>
          <tr>
            <th scope="col">Día</th>
            <th scope="col">Ejercicio</th>
            <th scope="col">Series x Repeticiones</th>
          </tr>
        </thead>
        <tbody>
          {RUTINA_EJEMPLO.map((fila) => (
            <tr key={fila.dia}>
              <td>{fila.dia}</td>
              <td>{fila.ejercicio}</td>
              <td>{fila.series}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
