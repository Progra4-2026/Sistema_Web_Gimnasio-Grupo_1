/* =========================================================
   Gimnasio PowerFit - Grupo 1
   contacto.js — Formulario de consultas (página Contactos)

   Une los tres módulos del Entregable 3:
     validaciones.js → valida antes de enviar (punto 6)
     api.js          → POST /api/consultas con async/await (punto 4)
     storage.js      → datos recordados, preferencias y borrador (punto 5)
   ========================================================= */

import { activarValidacion, validarFormulario } from './validaciones.js';
import { enviarConsulta } from './api.js';
import {
    obtenerPreferencias, guardarPreferencias,
    obtenerDatosUsuario, guardarDatosUsuario, olvidarDatosUsuario,
    obtenerBorrador, guardarBorrador, borrarBorrador
} from './storage.js';
import { leerFormulario, rellenarFormulario, mostrarMensaje, limpiarMensajes, estadoEnviando } from './formularios.js';

const formulario = document.getElementById('form-consulta');
const mensajes = document.getElementById('mensajes-consulta');
const casillaRecordar = document.getElementById('recordar-datos');
const selectAsunto = document.getElementById('asunto');
const botonEnviar = formulario.querySelector('button[type="submit"]');

/* ---------- Restaurar datos guardados ---------- */

function restaurarDatos() {
    const preferencias = obtenerPreferencias();

    // localStorage: preferencia de asunto y datos recordados de visitas anteriores
    selectAsunto.value = preferencias.asuntoConsulta;
    casillaRecordar.checked = preferencias.recordarDatos;
    if (preferencias.recordarDatos) {
        rellenarFormulario(formulario, obtenerDatosUsuario());
    }

    // sessionStorage: borrador de esta sesión (tiene prioridad, es lo más reciente)
    const borrador = obtenerBorrador(formulario.id);
    if (borrador) {
        rellenarFormulario(formulario, borrador);
        mostrarMensaje(mensajes, 'info', 'Recuperamos el borrador que tenías sin enviar.', { autoOcultarMs: 5000 });
    }
}

/* ---------- Eventos ---------- */

// input: cada vez que el usuario escribe se guarda el borrador en sessionStorage
formulario.addEventListener('input', () => {
    guardarBorrador(formulario.id, leerFormulario(formulario));
});

// change: se recuerda el asunto preferido en localStorage
selectAsunto.addEventListener('change', () => {
    guardarPreferencias({ asuntoConsulta: selectAsunto.value });
});

casillaRecordar.addEventListener('change', () => {
    guardarPreferencias({ recordarDatos: casillaRecordar.checked });
    if (!casillaRecordar.checked) olvidarDatosUsuario();
});

// submit: validar → enviar con fetch → mostrar resultado sin recargar
formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    limpiarMensajes(mensajes);

    if (!validarFormulario(formulario)) {
        mostrarMensaje(mensajes, 'error', 'Revisá los campos marcados en rojo antes de enviar.');
        return;
    }

    const datos = leerFormulario(formulario);
    const consulta = {
        nombre: datos.nombre,
        email: datos.email,
        telefono: datos.telefono,
        asunto: datos.asunto,
        mensaje: datos.mensaje
    };

    estadoEnviando(botonEnviar, true);
    try {
        const respuesta = await enviarConsulta(consulta);

        if (casillaRecordar.checked) {
            guardarDatosUsuario(consulta);
        }
        borrarBorrador(formulario.id);
        // reset() limpia todo; luego se vuelven a aplicar las preferencias guardadas
        const recordar = casillaRecordar.checked;
        formulario.reset();
        casillaRecordar.checked = recordar;
        selectAsunto.value = obtenerPreferencias().asuntoConsulta;
        if (recordar) rellenarFormulario(formulario, obtenerDatosUsuario());

        mostrarMensaje(mensajes, 'exito', respuesta?.mensaje ?? 'Consulta enviada correctamente.');
    } catch (error) {
        mostrarMensaje(mensajes, 'error', error.message);
    } finally {
        estadoEnviando(botonEnviar, false);
    }
});

/* ---------- Inicio ---------- */
activarValidacion(formulario);
restaurarDatos();
