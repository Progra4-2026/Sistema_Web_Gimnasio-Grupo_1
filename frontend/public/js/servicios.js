/* =========================================================
   Gimnasio PowerFit - Grupo 1
   servicios.js — Página Servicios

   - Inscripción: validación (punto 6) + POST /api/inscripciones (punto 4)
     + preferencias y borrador en storage (punto 5).
   - Cursos: GET /api/cursos (punto 4) con caché en sessionStorage (punto 5).
   - Resto de formularios (Rutinas, Mantenimiento, Desinscribirse):
     validación en el cliente; su conexión al backend llega con
     login y CRUD en los entregables 4 y 6.
   ========================================================= */

import { activarValidacion, validarFormulario } from './validaciones.js';
import { enviarInscripcion, obtenerCursos } from './api.js';
import {
    obtenerPreferencias, guardarPreferencias,
    obtenerDatosUsuario,
    obtenerBorrador, guardarBorrador, borrarBorrador,
    obtenerDeCache, guardarEnCache
} from './storage.js';
import { leerFormulario, rellenarFormulario, mostrarMensaje, limpiarMensajes, estadoEnviando } from './formularios.js';

/* =========================================================
   Inscripción
   ========================================================= */

const formInscripcion = document.getElementById('form-inscripcion');
const mensajesInscripcion = document.getElementById('mensajes-inscripcion');
const selectTipo = document.getElementById('ins-tipo');

function restaurarInscripcion() {
    const preferencias = obtenerPreferencias();
    selectTipo.value = preferencias.tipoInscripcion;

    // Si el usuario pidió recordar sus datos en Contactos, se reutilizan aquí
    if (preferencias.recordarDatos) {
        rellenarFormulario(formInscripcion, obtenerDatosUsuario());
    }

    const borrador = obtenerBorrador(formInscripcion.id);
    if (borrador) rellenarFormulario(formInscripcion, borrador);
}

formInscripcion.addEventListener('input', () => {
    // storage.js descarta el campo password automáticamente
    guardarBorrador(formInscripcion.id, leerFormulario(formInscripcion));
});

selectTipo.addEventListener('change', () => {
    guardarPreferencias({ tipoInscripcion: selectTipo.value });
});

formInscripcion.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    limpiarMensajes(mensajesInscripcion);

    if (!validarFormulario(formInscripcion)) {
        mostrarMensaje(mensajesInscripcion, 'error', 'Revisá los campos marcados en rojo antes de inscribirte.');
        return;
    }

    const datos = leerFormulario(formInscripcion);
    const inscripcion = {
        nombre: datos.nombre,
        cedula: datos.cedula,
        email: datos.email,
        telefono: datos.telefono,
        fechaNacimiento: datos.fecha_nacimiento,
        tipoInscripcion: datos.tipo_inscripcion,
        password: datos.password
    };

    const boton = formInscripcion.querySelector('button[type="submit"]');
    estadoEnviando(boton, true);
    try {
        const respuesta = await enviarInscripcion(inscripcion);
        borrarBorrador(formInscripcion.id);
        formInscripcion.reset();
        selectTipo.value = obtenerPreferencias().tipoInscripcion;
        mostrarMensaje(mensajesInscripcion, 'exito', respuesta?.mensaje ?? 'Inscripción registrada correctamente.');
    } catch (error) {
        // 409 → cédula duplicada; 400 → datos inválidos según el servidor
        mostrarMensaje(mensajesInscripcion, 'error', error.message);
        if (error.estado === 409) document.getElementById('ins-cedula').focus();
    } finally {
        estadoEnviando(boton, false);
    }
});

activarValidacion(formInscripcion);
restaurarInscripcion();

/* =========================================================
   Cursos (GET /api/cursos)
   ========================================================= */

const listaCursos = document.getElementById('lista-cursos');
const mensajesCursos = document.getElementById('mensajes-cursos');

function crearTarjetaCurso(curso) {
    const articulo = document.createElement('article');
    articulo.className = 'tarjeta tarjeta-curso';

    const imagen = document.createElement('img');
    imagen.src = `../assets/${curso.imagen}`;
    imagen.alt = `Clase de ${curso.descripcion}`;
    imagen.width = 320;
    imagen.height = 180;

    const cuerpo = document.createElement('div');
    cuerpo.className = 'tarjeta-curso__cuerpo';

    const etiqueta = document.createElement('p');
    etiqueta.className = 'etiqueta-id';
    etiqueta.textContent = `Curso #${curso.id}`;

    const titulo = document.createElement('h3');
    titulo.textContent = curso.descripcion;

    const detalle = document.createElement('p');
    detalle.textContent = curso.detalle;

    const lista = document.createElement('ul');
    lista.className = 'lista-detalles';
    lista.innerHTML = '<li><strong>Horario:</strong> </li><li><strong>Disponibilidad:</strong> </li>';
    lista.children[0].append(curso.horario);
    lista.children[1].append(curso.cupos > 0 ? `${curso.cupos} cupos` : 'Sin cupos');

    cuerpo.append(etiqueta, titulo, detalle, lista);
    articulo.append(imagen, cuerpo);
    return articulo;
}

function pintarCursos(cursos) {
    const fragmento = document.createDocumentFragment();
    for (const curso of cursos) {            // recorrido con el iterador de Array (for...of)
        fragmento.appendChild(crearTarjetaCurso(curso));
    }
    listaCursos.replaceChildren(fragmento);  // reemplaza las tarjetas de ejemplo del HTML
}

async function cargarCursos() {
    // sessionStorage: si ya se consultaron en esta sesión, no se vuelve a pedir
    const enCache = obtenerDeCache('cursos');
    if (enCache) {
        pintarCursos(enCache);
        return;
    }

    listaCursos.setAttribute('aria-busy', 'true');
    try {
        const cursos = await obtenerCursos();
        guardarEnCache('cursos', cursos);
        pintarCursos(cursos);
    } catch (error) {
        // Se dejan visibles las tarjetas de ejemplo del HTML
        mostrarMensaje(mensajesCursos, 'error', `No se pudieron cargar los cursos actualizados. ${error.message}`);
    } finally {
        listaCursos.removeAttribute('aria-busy');
    }
}

cargarCursos();

/* =========================================================
   Formularios sin backend todavía (validación en el cliente)
   ========================================================= */

const formulariosPendientes = [
    { id: 'form-login-rutinas', aviso: 'El inicio de sesión se habilitará con la autenticación del sistema (Entregable 6).' },
    { id: 'form-desinscripcion', aviso: 'La desinscripción se procesará en el servidor cuando esté disponible el backend completo.' },
    { id: 'form-mant-buscar', aviso: 'El mantenimiento se conectará al backend en el Entregable 4.' },
    { id: 'form-mant-agregar', aviso: 'El mantenimiento se conectará al backend en el Entregable 4.' },
    { id: 'form-mant-actualizar', aviso: 'El mantenimiento se conectará al backend en el Entregable 4.' },
    { id: 'form-mant-eliminar', aviso: 'El mantenimiento se conectará al backend en el Entregable 4.' }
];

for (const { id, aviso } of formulariosPendientes) {
    const formulario = document.getElementById(id);
    if (!formulario) continue;

    // Contenedor de mensajes creado dinámicamente al final del formulario
    const contenedorMensajes = document.createElement('div');
    contenedorMensajes.setAttribute('aria-live', 'polite');
    formulario.appendChild(contenedorMensajes);

    activarValidacion(formulario);

    formulario.addEventListener('submit', (evento) => {
        evento.preventDefault();
        if (!validarFormulario(formulario)) {
            mostrarMensaje(contenedorMensajes, 'error', 'Revisá los campos marcados en rojo.');
            return;
        }
        mostrarMensaje(contenedorMensajes, 'info', `Datos válidos. ${aviso}`, { autoOcultarMs: 6000 });
    });
}
