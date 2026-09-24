/* =========================================================
   Gimnasio PowerFit - Grupo 1
   formularios.js — Utilidades compartidas para formularios
   y mensajes al usuario (sin recargar la página, sin alert()).
   ========================================================= */

/** Devuelve los valores del formulario como objeto { name: valor } sin espacios extra. */
export function leerFormulario(formulario) {
    const datos = {};
    for (const [nombre, valor] of new FormData(formulario)) {
        datos[nombre] = typeof valor === 'string' ? valor.trim() : valor;
    }
    return datos;
}

/** Rellena los campos del formulario cuyo name coincida con una clave del objeto. */
export function rellenarFormulario(formulario, datos) {
    if (!datos) return;
    for (const [nombre, valor] of Object.entries(datos)) {
        const campo = formulario.elements.namedItem(nombre);
        if (campo && campo.type !== 'password' && campo.type !== 'checkbox' && valor != null) {
            campo.value = valor;
        }
    }
}

/**
 * Muestra un mensaje de éxito, error o información dentro de un contenedor.
 * Usa las clases .mensaje.exito / .mensaje.error de global.css.
 */
export function mostrarMensaje(contenedor, tipo, texto, { autoOcultarMs = 0 } = {}) {
    contenedor.replaceChildren();

    const mensaje = document.createElement('p');
    mensaje.className = `mensaje ${tipo}`;
    mensaje.setAttribute('role', tipo === 'error' ? 'alert' : 'status');
    mensaje.textContent = texto;

    const cerrar = document.createElement('button');
    cerrar.type = 'button';
    cerrar.className = 'mensaje__cerrar';
    cerrar.setAttribute('aria-label', 'Cerrar mensaje');
    cerrar.textContent = '×';
    cerrar.addEventListener('click', () => mensaje.remove());
    mensaje.appendChild(cerrar);

    contenedor.appendChild(mensaje);

    if (autoOcultarMs > 0) {
        setTimeout(() => mensaje.remove(), autoOcultarMs);
    }
}

export function limpiarMensajes(contenedor) {
    contenedor.replaceChildren();
}

/** Bloquea el botón de envío mientras se espera la respuesta del servidor. */
export function estadoEnviando(boton, enviando, textoEnviando = 'Enviando...') {
    if (enviando) {
        boton.dataset.textoOriginal = boton.textContent;
        boton.textContent = textoEnviando;
        boton.disabled = true;
        boton.setAttribute('aria-busy', 'true');
    } else {
        boton.textContent = boton.dataset.textoOriginal || boton.textContent;
        boton.disabled = false;
        boton.removeAttribute('aria-busy');
    }
}

/**
 * Crea un bloque .campo-formulario (label + control) listo para insertar en el DOM.
 * @param {string} etiqueta texto del label
 * @param {HTMLElement} control input, select o textarea ya configurado (con id)
 */
export function crearCampo(etiqueta, control) {
    const contenedor = document.createElement('div');
    contenedor.className = 'campo-formulario';

    const label = document.createElement('label');
    label.htmlFor = control.id;
    label.textContent = `${etiqueta} `;
    if (control.required) {
        const obligatorio = document.createElement('span');
        obligatorio.className = 'obligatorio';
        obligatorio.textContent = '*';
        label.appendChild(obligatorio);
    }

    contenedor.append(label, control);
    return contenedor;
}

/** Crea un <select> con una opción vacía inicial y las opciones [valor, texto] recibidas. */
export function crearSelect({ id, name, required = false, textoVacio = 'Seleccione una opción', opciones = [] }) {
    const select = document.createElement('select');
    select.id = id;
    select.name = name;
    select.required = required;
    llenarSelect(select, opciones, textoVacio);
    return select;
}

/** Reemplaza las opciones de un <select>, conservando el valor elegido si sigue existiendo. */
export function llenarSelect(select, opciones, textoVacio = 'Seleccione una opción') {
    const valorActual = select.value;
    const nuevas = [new Option(textoVacio, '')];
    for (const { valor, texto, deshabilitada = false } of opciones) {
        const opcion = new Option(texto, valor);
        opcion.disabled = deshabilitada;
        nuevas.push(opcion);
    }
    select.replaceChildren(...nuevas);
    select.value = valorActual;
    if (select.selectedIndex === -1) select.value = '';
}

/**
 * Agrega un botón "Mostrar / Ocultar" a cada campo de contraseña del formulario.
 * Evento click: alterna el type del input entre password y text.
 */
export function activarMostrarPassword(formulario) {
    for (const campo of formulario.querySelectorAll('input[type="password"]')) {
        const boton = document.createElement('button');
        boton.type = 'button';
        boton.className = 'boton-texto';
        boton.textContent = 'Mostrar contraseña';
        boton.setAttribute('aria-controls', campo.id);
        boton.setAttribute('aria-pressed', 'false');

        boton.addEventListener('click', () => {
            const visible = campo.type === 'password';
            campo.type = visible ? 'text' : 'password';
            boton.textContent = visible ? 'Ocultar contraseña' : 'Mostrar contraseña';
            boton.setAttribute('aria-pressed', String(visible));
        });

        // Al limpiar el formulario la contraseña vuelve a ocultarse
        formulario.addEventListener('reset', () => {
            campo.type = 'password';
            boton.textContent = 'Mostrar contraseña';
            boton.setAttribute('aria-pressed', 'false');
        });

        campo.after(boton);
    }
}
