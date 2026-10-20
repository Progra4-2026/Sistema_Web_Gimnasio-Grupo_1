import './Cards.css';

export default function InstalacionCard({ titulo, imagen }) {
  return (
    <figure className="tarjeta tarjeta-instalacion">
      <img src={imagen} alt={titulo} />
      <figcaption>{titulo}</figcaption>
    </figure>
  );
}
