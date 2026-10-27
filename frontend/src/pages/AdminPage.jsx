import { useEffect, useState } from 'react';
import {
  listarClientes, eliminarCliente,
  listarInstructores, eliminarInstructor,
  listarAdministradores, eliminarAdministrador,
  listarParametros, actualizarParametro,
} from '../api/admin';
import { useNotificacion } from '../context/NotificationContext';
import Modal from '../components/common/Modal';
import InstructorAdminForm from '../components/forms/InstructorAdminForm';
import AdministradorForm from '../components/forms/AdministradorForm';
import './Pages.css';

function useListado(cargarFn) {
  const [datos, setDatos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  function recargar() {
    cargarFn().then(setDatos).catch((err) => setError(err.message)).finally(() => setCargando(false));
  }

  useEffect(() => {
    recargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return { datos, cargando, error, recargar };
}

function SeccionParametros() {
  const { datos: parametros, cargando, error, recargar } = useListado(listarParametros);
  const [valores, setValores] = useState({});
  const { notificar } = useNotificacion();

  async function guardar(clave) {
    try {
      await actualizarParametro(clave, { clave, valor: valores[clave] });
      notificar('exito', 'Parámetro actualizado correctamente.');
      recargar();
    } catch (err) {
      notificar('error', err.message);
    }
  }

  if (cargando) return <p>Cargando parámetros...</p>;
  if (error) return <p className="texto-error">{error}</p>;

  return (
    <table className="tabla-datos">
      <thead>
        <tr>
          <th scope="col">Parámetro</th>
          <th scope="col">Valor</th>
          <th scope="col">Acciones</th>
        </tr>
      </thead>
      <tbody>
        {parametros.map((p) => (
          <tr key={p.clave}>
            <td>{p.descripcion || p.clave}</td>
            <td>
              <input
                value={valores[p.clave] ?? p.valor}
                onChange={(e) => setValores((anterior) => ({ ...anterior, [p.clave]: e.target.value }))}
              />
            </td>
            <td>
              <button type="button" className="boton boton-secundario" onClick={() => guardar(p.clave)}>Guardar</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function SeccionClientes() {
  const { datos: clientes, cargando, error, recargar } = useListado(listarClientes);
  const { notificar } = useNotificacion();

  async function eliminar(cedula) {
    try {
      await eliminarCliente(cedula);
      notificar('exito', 'Cliente eliminado correctamente.');
      recargar();
    } catch (err) {
      notificar('error', err.message);
    }
  }

  if (cargando) return <p>Cargando clientes...</p>;
  if (error) return <p className="texto-error">{error}</p>;
  if (clientes.length === 0) return <p>No hay clientes registrados.</p>;

  return (
    <table className="tabla-datos">
      <thead>
        <tr>
          <th scope="col">Cédula</th>
          <th scope="col">Nombre</th>
          <th scope="col">Correo</th>
          <th scope="col">Acciones</th>
        </tr>
      </thead>
      <tbody>
        {clientes.map((c) => (
          <tr key={c.cedula}>
            <td>{c.cedula}</td>
            <td>{c.nombre} {c.apellido1} {c.apellido2}</td>
            <td>{c.email}</td>
            <td>
              <button type="button" className="boton boton-peligro" onClick={() => eliminar(c.cedula)}>Eliminar</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function SeccionInstructores() {
  const { datos: instructores, cargando, error, recargar } = useListado(listarInstructores);
  const [formAbierto, setFormAbierto] = useState(false);
  const { notificar } = useNotificacion();

  async function eliminar(codInstructor) {
    try {
      await eliminarInstructor(codInstructor);
      notificar('exito', 'Instructor eliminado correctamente.');
      recargar();
    } catch (err) {
      notificar('error', err.message);
    }
  }

  return (
    <>
      <button type="button" className="boton" onClick={() => setFormAbierto(true)}>Nuevo instructor</button>
      {cargando ? <p>Cargando instructores...</p> : error ? <p className="texto-error">{error}</p> : (
        <table className="tabla-datos">
          <thead>
            <tr>
              <th scope="col">Código</th>
              <th scope="col">Nombre</th>
              <th scope="col">Correo</th>
              <th scope="col">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {instructores.map((i) => (
              <tr key={i.codInstructor}>
                <td>{i.codInstructor}</td>
                <td>{i.nombre} {i.apellido1} {i.apellido2}</td>
                <td>{i.email}</td>
                <td>
                  <button type="button" className="boton boton-peligro" onClick={() => eliminar(i.codInstructor)}>Eliminar</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <Modal abierto={formAbierto} titulo="Nuevo instructor" onCerrar={() => setFormAbierto(false)}>
        <InstructorAdminForm onGuardado={() => { setFormAbierto(false); recargar(); }} />
      </Modal>
    </>
  );
}

function SeccionAdministradores() {
  const { datos: administradores, cargando, error, recargar } = useListado(listarAdministradores);
  const [formAbierto, setFormAbierto] = useState(false);
  const { notificar } = useNotificacion();

  async function eliminar(codAdministrador) {
    try {
      await eliminarAdministrador(codAdministrador);
      notificar('exito', 'Administrador eliminado correctamente.');
      recargar();
    } catch (err) {
      notificar('error', err.message);
    }
  }

  return (
    <>
      <button type="button" className="boton" onClick={() => setFormAbierto(true)}>Nuevo administrador</button>
      {cargando ? <p>Cargando administradores...</p> : error ? <p className="texto-error">{error}</p> : (
        <table className="tabla-datos">
          <thead>
            <tr>
              <th scope="col">Código</th>
              <th scope="col">Nombre</th>
              <th scope="col">Correo</th>
              <th scope="col">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {administradores.map((a) => (
              <tr key={a.codAdministrador}>
                <td>{a.codAdministrador}</td>
                <td>{a.nombre}</td>
                <td>{a.email}</td>
                <td>
                  <button type="button" className="boton boton-peligro" onClick={() => eliminar(a.codAdministrador)}>Eliminar</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <Modal abierto={formAbierto} titulo="Nuevo administrador" onCerrar={() => setFormAbierto(false)}>
        <AdministradorForm onGuardado={() => { setFormAbierto(false); recargar(); }} />
      </Modal>
    </>
  );
}

export default function AdminPage() {
  return (
    <div className="contenedor seccion">
      <div className="titulo-seccion">
        <h1>Panel del administrador</h1>
        <p>Gestioná usuarios (clientes, instructores y administradores) y los parámetros del sistema.</p>
      </div>

      <section className="seccion-servicio">
        <h2>Parámetros del sistema</h2>
        <SeccionParametros />
      </section>

      <section className="seccion-servicio">
        <h2>Clientes</h2>
        <SeccionClientes />
      </section>

      <section className="seccion-servicio">
        <h2>Instructores</h2>
        <SeccionInstructores />
      </section>

      <section className="seccion-servicio">
        <h2>Administradores</h2>
        <SeccionAdministradores />
      </section>
    </div>
  );
}
