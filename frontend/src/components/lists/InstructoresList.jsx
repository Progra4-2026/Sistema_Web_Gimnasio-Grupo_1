import InstructorCard from '../cards/InstructorCard';

const INSTRUCTORES = [
  { nombre: 'Carlos Jiménez', especialidad: 'Entrenador de fuerza', imagen: '/assets/profile_pic.jpg' },
  { nombre: 'Ana Vargas', especialidad: 'Instructora de yoga y flexibilidad', imagen: '/assets/profile_pic.jpg' },
  { nombre: 'Luis Rojas', especialidad: 'Entrenador de CrossFit', imagen: '/assets/profile_pic.jpg' },
];

export default function InstructoresList() {
  return (
    <div className="rejilla rejilla--estrecha">
      {INSTRUCTORES.map((instructor) => (
        <InstructorCard key={instructor.nombre} instructor={instructor} />
      ))}
    </div>
  );
}
