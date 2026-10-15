import './Cards.css';

export default function InstructorCard({ instructor }) {
  return (
    <article className="tarjeta tarjeta-instructor">
      <img src={instructor.imagen} alt={`Foto de ${instructor.nombre}`} />
      <h3>{instructor.nombre}</h3>
      <p className="texto-ayuda">{instructor.especialidad}</p>
    </article>
  );
}
