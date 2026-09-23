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
