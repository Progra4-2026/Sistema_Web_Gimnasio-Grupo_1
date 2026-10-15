import { Link } from 'react-router-dom';
import './Footer.css';

export default function Footer() {
  return (
    <footer className="pie-sitio">
      <div className="contenedor pie-sitio__grid">
        <div>
          <h2>Gimnasio PowerFit</h2>
          <p>Tu mejor versión empieza aquí.</p>
        </div>

        <div>
          <h2>Enlaces</h2>
          <ul>
            <li><Link to="/">Inicio</Link></li>
            <li><Link to="/contacto">Contactos</Link></li>
            <li><Link to="/servicios">Servicios</Link></li>
            <li><Link to="/blog">Blog</Link></li>
          </ul>
        </div>

        <div>
          <h2>Horario</h2>
          <p>
            Lun-Vie: 5:00 a.m. - 9:00 p.m.
            <br />
            Sáb: 7:00 a.m. - 2:00 p.m.
          </p>
        </div>
      </div>
      <p className="derechos">&copy; 2026 Gimnasio PowerFit - Grupo 1. Proyecto académico Programación 4, Universidad Nacional.</p>
    </footer>
  );
}
