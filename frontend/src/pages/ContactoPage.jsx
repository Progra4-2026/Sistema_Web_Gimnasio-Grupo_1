import ConsultaForm from '../components/forms/ConsultaForm';
import './Pages.css';

export default function ContactoPage() {
  return (
    <section className="seccion contenedor">
      <div className="titulo-seccion">
        <h1>Contáctenos</h1>
        <p>Escribinos, llamanos o visitanos. Con gusto te atendemos.</p>
      </div>

      <div className="pagina-contacto__grid">
        <section className="tarjeta pagina-contacto__info">
          <h2>Información de contacto</h2>
          <address className="lista-datos-contacto">
            <p>
              <strong>Dirección:</strong>
              <br />
              200 metros norte del parque central, Cartago, Costa Rica
            </p>
            <p>
              <strong>Teléfono:</strong>
              <br />
              <a href="tel:+50622223333">+506 2222-3333</a>
            </p>
            <p>
              <strong>Correo electrónico:</strong>
              <br />
              <a href="mailto:info@gimnasiopowerfit.cr">info@gimnasiopowerfit.cr</a>
            </p>
          </address>

          <aside>
            <h3>Antes de venir</h3>
            <p className="texto-ayuda">
              Recomendamos llegar 10 minutos antes de tu primera sesión para completar el registro en recepción.
            </p>
          </aside>
        </section>

        <section className="tarjeta pagina-contacto__formulario">
          <h2>Formulario de consultas</h2>
          <p className="texto-ayuda">Los campos marcados con <span className="obligatorio">*</span> son obligatorios.</p>
          <ConsultaForm />
        </section>
      </div>
    </section>
  );
}
