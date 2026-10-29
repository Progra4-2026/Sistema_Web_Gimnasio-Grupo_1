import './Cards.css';

export default function MaquinaCard({ maquina }) {
  return (
    <article className="tarjeta tarjeta-maquina">
      <img src={maquina.urlImagen} alt={maquina.descripcion} />
      <h3>{maquina.descripcion}</h3>
      <p>
        <span className={`etiqueta-estado estado-${maquina.estado.replace(' ', '-')}`}>{maquina.estado}</span>
      </p>
    </article>
  );
}
