import { useState } from 'react';
import { crearInscripcion } from '../../api/inscripciones';
import { useNotificacion } from '../../context/NotificationContext';
import './Forms.css';

const VALOR_INICIAL = {
  nombre: '',
  cedula: '',
  email: '',
  telefono: '',
  fechaNacimiento: '',
  tipoInscripcion: '',
  idCurso: '',
  objetivo: '',
  password: '',
};

// Deben coincidir con los objetivos que acepta el backend (InscripcionServiceImpl.OBJETIVOS)
const OBJETIVOS_RUTINA = [
  { valor: 'fuerza', texto: 'Ganar fuerza y masa muscular' },
  { valor: 'resistencia', texto: 'Mejorar la resistencia cardiovascular' },
  { valor: 'peso', texto: 'Bajar de peso' },
  { valor: 'flexibilidad', texto: 'Flexibilidad y movilidad' },
];

const PATRON_NOMBRE = /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{3,80}$/;
const PATRON_CEDULA = /^[0-9]{9,12}$/;
const PATRON_TELEFONO = /^[0-9]{4}-?[0-9]{4}$/;

function calcularEdad(fechaNacimiento) {
  const nacimiento = new Date(fechaNacimiento);
  const hoy = new Date();
  let edad = hoy.getFullYear() - nacimiento.getFullYear();
  const aunNoCumple = hoy.getMonth() < nacimiento.getMonth() ||
    (hoy.getMonth() === nacimiento.getMonth() && hoy.getDate() < nacimiento.getDate());
  if (aunNoCumple) edad -= 1;
  return edad;
}

function validar(datos) {
  const errores = {};

  if (!PATRON_NOMBRE.test(datos.nombre)) errores.nombre = 'Ingresá solo letras y espacios (3 a 80 caracteres).';
  if (!PATRON_CEDULA.test(datos.cedula)) errores.cedula = 'Solo números, entre 9 y 12 dígitos.';
  if (!/^\S+@\S+\.\S+$/.test(datos.email)) errores.email = 'Ingresá un correo válido.';
  if (!PATRON_TELEFONO.test(datos.telefono)) errores.telefono = 'Formato: 8888-8888.';
  if (!datos.fechaNacimiento) errores.fechaNacimiento = 'Ingresá tu fecha de nacimiento.';
  else if (calcularEdad(datos.fechaNacimiento) < 14) errores.fechaNacimiento = 'Edad mínima para inscribirse: 14 años.';
  if (!datos.tipoInscripcion) errores.tipoInscripcion = 'Seleccioná un tipo de inscripción.';
  if (datos.tipoInscripcion === 'curso' && !datos.idCurso) errores.idCurso = 'Seleccioná un curso.';
  if (datos.tipoInscripcion === 'rutina' && !datos.objetivo) errores.objetivo = 'Seleccioná tu objetivo.';
  if (datos.password.length < 8) errores.password = 'Mínimo 8 caracteres.';

  return errores;
}

export default function InscripcionForm({ cursos, cursoSeleccionadoId, onInscripcionExitosa }) {
  const [datos, setDatos] = useState(VALOR_INICIAL);
  const [errores, setErrores] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [ultimoCursoAplicado, setUltimoCursoAplicado] = useState(null);
  const { notificar } = useNotificacion();

  if (cursoSeleccionadoId && cursoSeleccionadoId !== ultimoCursoAplicado) {
    setUltimoCursoAplicado(cursoSeleccionadoId);
    setDatos((anterior) => ({ ...anterior, tipoInscripcion: 'curso', idCurso: String(cursoSeleccionadoId) }));
  }

  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setDatos((anterior) => ({ ...anterior, [name]: value }));
  }

  async function manejarEnvio(evento) {
    evento.preventDefault();
    const erroresEncontrados = validar(datos);
    setErrores(erroresEncontrados);
    if (Object.keys(erroresEncontrados).length > 0) return;

    setEnviando(true);
    try {
      const respuesta = await crearInscripcion({
        ...datos,
        idCurso: datos.tipoInscripcion === 'curso' ? datos.idCurso : null,
        objetivo: datos.tipoInscripcion === 'rutina' ? datos.objetivo : null,
      });
      notificar('exito', respuesta.mensaje);
      setDatos(VALOR_INICIAL);
      setErrores({});
      onInscripcionExitosa?.(respuesta.inscripcion);
    } catch (error) {
      setErrores(error.errores || {});
      notificar('error', error.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={manejarEnvio}>
      <div className="campo-formulario">
        <label htmlFor="ins-nombre">Nombre completo <span className="obligatorio">*</span></label>
        <input id="ins-nombre" name="nombre" value={datos.nombre} onChange={manejarCambio} placeholder="Ej: Juan Pérez Alvarado" />
        {errores.nombre && <p className="texto-error">{errores.nombre}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="ins-cedula">Cédula / Identificación <span className="obligatorio">*</span></label>
        <input id="ins-cedula" name="cedula" value={datos.cedula} onChange={manejarCambio} placeholder="Ej: 304560789" />
        {errores.cedula && <p className="texto-error">{errores.cedula}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="ins-email">Correo electrónico <span className="obligatorio">*</span></label>
        <input id="ins-email" name="email" value={datos.email} onChange={manejarCambio} placeholder="ejemplo@correo.com" />
        {errores.email && <p className="texto-error">{errores.email}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="ins-telefono">Teléfono <span className="obligatorio">*</span></label>
        <input id="ins-telefono" name="telefono" value={datos.telefono} onChange={manejarCambio} placeholder="8888-8888" />
        {errores.telefono && <p className="texto-error">{errores.telefono}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="ins-nacimiento">Fecha de nacimiento <span className="obligatorio">*</span></label>
        <input type="date" id="ins-nacimiento" name="fechaNacimiento" value={datos.fechaNacimiento} onChange={manejarCambio} />
        {errores.fechaNacimiento && <p className="texto-error">{errores.fechaNacimiento}</p>}
      </div>

      <div className="campo-formulario">
        <label htmlFor="ins-tipo">Tipo de inscripción <span className="obligatorio">*</span></label>
        <select id="ins-tipo" name="tipoInscripcion" value={datos.tipoInscripcion} onChange={manejarCambio}>
          <option value="">Seleccione una opción</option>
          <option value="rutina">Rutina personalizada</option>
          <option value="curso">Curso grupal</option>
        </select>
        {errores.tipoInscripcion && <p className="texto-error">{errores.tipoInscripcion}</p>}
      </div>

      {datos.tipoInscripcion === 'curso' && (
        <div className="campo-formulario">
          <label htmlFor="ins-curso">Curso <span className="obligatorio">*</span></label>
          <select id="ins-curso" name="idCurso" value={datos.idCurso} onChange={manejarCambio}>
            <option value="">Seleccione un curso</option>
            {cursos.map((curso) => (
              <option key={curso.id} value={curso.id} disabled={Number(curso.cupos) <= 0}>
                {Number(curso.cupos) <= 0 ? `${curso.descripcion} (sin cupos)` : curso.descripcion}
              </option>
            ))}
          </select>
          {errores.idCurso && <p className="texto-error">{errores.idCurso}</p>}
        </div>
      )}

      {datos.tipoInscripcion === 'rutina' && (
        <div className="campo-formulario">
          <label htmlFor="ins-objetivo">Objetivo <span className="obligatorio">*</span></label>
          <select id="ins-objetivo" name="objetivo" value={datos.objetivo} onChange={manejarCambio}>
            <option value="">Seleccioná tu objetivo</option>
            {OBJETIVOS_RUTINA.map((objetivo) => (
              <option key={objetivo.valor} value={objetivo.valor}>{objetivo.texto}</option>
            ))}
          </select>
          {errores.objetivo && <p className="texto-error">{errores.objetivo}</p>}
        </div>
      )}

      <div className="campo-formulario">
        <label htmlFor="ins-password">Contraseña <span className="obligatorio">*</span></label>
        <input type="password" id="ins-password" name="password" value={datos.password} onChange={manejarCambio} placeholder="Mínimo 8 caracteres" />
        <span className="texto-ayuda">Se almacenará cifrada, nunca en texto plano.</span>
        {errores.password && <p className="texto-error">{errores.password}</p>}
      </div>

      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Enviando...' : 'Inscribirme'}
      </button>
    </form>
  );
}
