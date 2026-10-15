import './Pages.css';

const SECCIONES_BLOG = [
  {
    titulo: 'Salud y ejercicio',
    enlaces: [
      {
        href: 'https://www.mayoclinic.org/healthy-lifestyle/fitness/basics/fitness-basics/hlv-20049447',
        texto: 'Fitness basics — Mayo Clinic',
        descripcion: 'Guía general sobre los fundamentos del ejercicio físico.',
      },
      {
        href: 'https://www.who.int/news-room/fact-sheets/detail/physical-activity',
        texto: 'Actividad física — Organización Mundial de la Salud',
        descripcion: 'Recomendaciones oficiales de actividad física por edad.',
      },
      {
        href: 'https://www.health.harvard.edu/topics/exercise-and-fitness',
        texto: 'Exercise & Fitness — Harvard Health',
        descripcion: 'Artículos sobre entrenamiento y prevención de lesiones.',
      },
    ],
  },
  {
    titulo: 'Rutinas de ejercicio',
    enlaces: [
      {
        href: 'https://www.acefitness.org/resources/everyone/exercise-library/',
        texto: 'Workout library — ACE Fitness',
        descripcion: 'Rutinas guiadas por nivel y grupo muscular.',
      },
      {
        href: 'https://www.nhs.uk/live-well/exercise/',
        texto: 'Exercise — NHS',
        descripcion: 'Planes de ejercicio para principiantes y nivel intermedio.',
      },
    ],
  },
  {
    titulo: 'Recetas saludables y dietas',
    enlaces: [
      {
        href: 'https://www.myplate.gov/myplate-kitchen',
        texto: 'MyPlate Kitchen — USDA',
        descripcion: 'Recetas balanceadas basadas en las guías alimentarias.',
      },
      {
        href: 'https://www.eatright.org/food',
        texto: 'Food & Nutrition — Academy of Nutrition and Dietetics',
        descripcion: 'Consejos de alimentación saludable respaldados por nutricionistas.',
      },
      {
        href: 'https://www.who.int/news-room/fact-sheets/detail/healthy-diet',
        texto: 'Dieta saludable — Organización Mundial de la Salud',
        descripcion: 'Principios básicos de una alimentación equilibrada.',
      },
    ],
  },
];

export default function BlogPage() {
  return (
    <section className="seccion contenedor">
      <div className="titulo-seccion">
        <h1>Blog de salud y bienestar</h1>
        <p>Una selección de artículos externos sobre ejercicio, nutrición y hábitos saludables.</p>
      </div>

      {SECCIONES_BLOG.map((seccion) => (
        <section key={seccion.titulo} className="bloque-blog">
          <h2>{seccion.titulo}</h2>
          <ul className="lista-blog">
            {seccion.enlaces.map((enlace) => (
              <li key={enlace.href} className="tarjeta">
                <a href={enlace.href} target="_blank" rel="noopener noreferrer">{enlace.texto}</a>
                <p className="texto-ayuda">{enlace.descripcion}</p>
              </li>
            ))}
          </ul>
        </section>
      ))}
    </section>
  );
}
