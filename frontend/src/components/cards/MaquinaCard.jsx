import './Cards.css';

export default function MaquinaCard({ maquina }) {
  return (
    <article className="tarjeta tarjeta-maquina">
      <img src={maquina.imagen} alt={maquina.nombre} />
      <h3>{maquina.nombre}</h3>
    </article>
  );
}
