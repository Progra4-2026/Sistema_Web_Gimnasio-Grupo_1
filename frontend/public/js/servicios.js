/* =========================================================
   Gimnasio PowerFit - Grupo 1
   servicios.js — Página Servicios

   - Inscripción: validación (punto 6) + POST /api/inscripciones (punto 4)
     + preferencias y borrador en storage (punto 5).
   - Campos dinámicos (punto 3): según el tipo de inscripción se crea el
     select de curso o el de objetivo de rutina; en Desinscribirse, el
     motivo "Otro" agrega un campo para detallarlo.
   - Cursos: GET /api/cursos (punto 4) con caché en sessionStorage (punto 5).
   - Resto de formularios (Rutinas, Mantenimiento, Desinscribirse):
     validación en el cliente; su conexión al backend llega con
     login y CRUD en los entregables 4 y 6.
   ========================================================= */

import { activarValidacion, activarValidacionCampo, validarFormulario } from './validaciones.js';
import { enviarInscripcion, obtenerCursos } from './api.js';
import {
    obtenerPreferencias, guardarPreferencias,
    obtenerDatosUsuario,
    obtenerBorrador, guardarBorrador, borrarBorrador,
    obtenerDeCache, guardarEnCache
} from './storage.js';
import {
    leerFormulario, rellenarFormulario, mostrarMensaje, limpiarMensajes, estadoEnviando,
    crearCampo, crearSelect, llenarSelect, activarMostrarPassword
} from './formularios.js';

/* =========================================================
   Inscripción
   ========================================================= */

const formInscripcion = document.getElementById('form-inscripcion');
const mensajesInscripcion = document.getElementById('mensajes-inscripcion');
const selectTipo = document.getElementById('ins-tipo');
const listaCursos = document.getElementById('lista-cursos');
const mensajesCursos = document.getElementById('mensajes-cursos');

const OBJETIVOS_RUTINA = [
    { valor: 'fuerza', texto: 'Ganar fuerza y masa muscular' },
    { valor: 'resistencia', texto: 'Mejorar la resistencia cardiovascular' },
    { valor: 'peso', texto: 'Bajar de peso' },
    { valor: 'flexibilidad', texto: 'Flexibilidad y movilidad' }
];

// Cursos para el select de inscripción: se parte de las tarjetas del HTML
// y se reemplazan por los del backend cuando cargarCursos() los obtiene.
let cursosDisponibles = Array.from(listaCursos.querySelectorAll('.tarjeta-curso'), (tarjeta) => ({
    id: Number.parseInt(tarjeta.querySelector('.etiqueta-id').textContent.replace(/\D/g, ''), 10),
    descripcion: tarjeta.querySelector('h3').textContent,
    cupos: null
}));

function opcionesDeCursos() {
    return cursosDisponibles.map((curso) => ({
        valor: String(curso.id),
        texto: curso.cupos === 0 ? `${curso.descripcion} (sin cupos)` : curso.descripcion,
        deshabilitada: curso.cupos === 0
    }));
}

/** Quita el campo dinámico (si existe) junto con su contenedor. */
function eliminarCampo(id) {
    document.getElementById(id)?.closest('.campo-formulario').remove();
}

/**
 * change de "Tipo de inscripción": crea el campo que corresponde a la opción
 * elegida (curso → select de cursos, rutina → objetivo) y elimina el otro.
 */
function actualizarCamposTipo() {
    const tipo = selectTipo.value;
    const campoTipo = selectTipo.closest('.campo-formulario');

    if (tipo !== 'curso') eliminarCampo('ins-curso');
    if (tipo !== 'rutina') eliminarCampo('ins-objetivo');

    if (tipo === 'curso' && !document.getElementById('ins-curso')) {
        const select = crearSelect({
            id: 'ins-curso', name: 'id_curso', required: true,
            textoVacio: 'Seleccioná un curso', opciones: opcionesDeCursos()
        });
        campoTipo.after(crearCampo('Curso', select));
        activarValidacionCampo(select);
    }

    if (tipo === 'rutina' && !document.getElementById('ins-objetivo')) {
        const select = crearSelect({
            id: 'ins-objetivo', name: 'objetivo', required: true,
            textoVacio: 'Seleccioná tu objetivo', opciones: OBJETIVOS_RUTINA
        });
        campoTipo.after(crearCampo('Objetivo de la rutina', select));
        activarValidacionCampo(select);
    }
}

function restaurarInscripcion() {
    const preferencias = obtenerPreferencias();
    selectTipo.value = preferencias.tipoInscripcion;

    // Si el usuario pidió recordar sus datos en Contactos, se reutilizan aquí
    if (preferencias.recordarDatos) {
        rellenarFormulario(formInscripcion, obtenerDatosUsuario());
    }

    const borrador = obtenerBorrador(formInscripcion.id);
    if (borrador) rellenarFormulario(formInscripcion, borrador);

    // Se crean los campos del tipo restaurado y se rellenan con el borrador
    actualizarCamposTipo();
    if (borrador) rellenarFormulario(formInscripcion, borrador);
}

formInscripcion.addEventListener('input', () => {
    // storage.js descarta el campo password automáticamente
    guardarBorrador(formInscripcion.id, leerFormulario(formInscripcion));
});

selectTipo.addEventListener('change', () => {
    guardarPreferencias({ tipoInscripcion: selectTipo.value });
    actualizarCamposTipo();
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
        idCurso: datos.id_curso ?? null,
        objetivo: datos.objetivo ?? null,
        password: datos.password
    };

    const boton = formInscripcion.querySelector('button[type="submit"]');
    estadoEnviando(boton, true);
    try {
        const respuesta = await enviarInscripcion(inscripcion);
        borrarBorrador(formInscripcion.id);
        formInscripcion.reset();
        selectTipo.value = obtenerPreferencias().tipoInscripcion;
        actualizarCamposTipo();
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
activarMostrarPassword(formInscripcion);
restaurarInscripcion();

/* =========================================================
   Cursos (GET /api/cursos)
   ========================================================= */

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

    // El select de cursos de la inscripción (si está visible) se actualiza con los cupos reales
    cursosDisponibles = cursos;
    const selectCurso = document.getElementById('ins-curso');
    if (selectCurso) llenarSelect(selectCurso, opcionesDeCursos(), 'Seleccioná un curso');
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
    activarMostrarPassword(formulario);

    formulario.addEventListener('submit', (evento) => {
        evento.preventDefault();
        if (!validarFormulario(formulario)) {
            mostrarMensaje(contenedorMensajes, 'error', 'Revisá los campos marcados en rojo.');
            return;
        }
        mostrarMensaje(contenedorMensajes, 'info', `Datos válidos. ${aviso}`, { autoOcultarMs: 6000 });
    });
}

/* =========================================================
   Desinscribirse: motivo "Otro"
   ========================================================= */

const selectMotivo = document.getElementById('desin-motivo');

// change: si el motivo es "Otro" se crea un campo obligatorio para detallarlo;
// con cualquier otra opción ese campo se elimina del DOM.
selectMotivo.addEventListener('change', () => {
    const existe = document.getElementById('desin-detalle');

    if (selectMotivo.value === 'otro' && !existe) {
        const detalle = document.createElement('textarea');
        detalle.id = 'desin-detalle';
        detalle.name = 'motivo_detalle';
        detalle.rows = 3;
        detalle.required = true;
        detalle.minLength = 10;
        detalle.maxLength = 300;
        detalle.placeholder = 'Contanos brevemente el motivo';

        selectMotivo.closest('.campo-formulario').after(crearCampo('Detalle del motivo', detalle));
        activarValidacionCampo(detalle);
        detalle.focus();
    } else if (selectMotivo.value !== 'otro' && existe) {
        eliminarCampo('desin-detalle');
    }
});

document.getElementById('form-desinscripcion').addEventListener('reset', () => eliminarCampo('desin-detalle'));
