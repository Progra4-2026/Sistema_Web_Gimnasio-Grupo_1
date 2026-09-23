/* =========================================================
   Gimnasio PowerFit - Grupo 1
   validaciones.js — Validación de formularios en el cliente
   (Entregable 3, punto 6)

   - Campos obligatorios, correo electrónico, teléfono,
     longitud mínima/máxima, patrones y rangos de fecha/número.
   - Retroalimentación visual: borde rojo/verde, mensaje bajo
     cada campo y contador de caracteres. Sin alert().
   - Las reglas se leen de los atributos HTML5 que ya tienen
     los formularios (required, type, minlength, maxlength,
     pattern, min, max), así HTML5 y JS validan lo mismo.
   ========================================================= */

export const EXPRESIONES = Object.freeze({
    // texto@dominio.ext — sin espacios, con al menos un punto en el dominio
    email: /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/,
    // Teléfono de Costa Rica: 8 dígitos, guion opcional (8888-8888 u 88888888)
    telefono: /^[0-9]{4}-?[0-9]{4}$/,
    // Cédula / identificación: solo números, entre 9 y 12 dígitos
    cedula: /^[0-9]{9,12}$/
});

const CLASE_ERROR = 'campo-formulario--error';
const CLASE_VALIDO = 'campo-formulario--valido';

/* ---------- Reglas individuales (reutilizables) ---------- */

export function esVacio(valor) {
    return valor === null || valor === undefined || String(valor).trim() === '';
}

export function esEmailValido(valor) {
    return EXPRESIONES.email.test(String(valor).trim());
}

export function esTelefonoValido(valor) {
    return EXPRESIONES.telefono.test(String(valor).trim());
}

export function esCedulaValida(valor) {
    return EXPRESIONES.cedula.test(String(valor).trim());
}

/**
 * Verifica que la longitud del texto esté entre min y max (inclusive).
 * Si min o max no vienen, no se valida ese extremo.
 */
export function cumpleLongitud(valor, min, max) {
    const largo = String(valor).trim().length;
    if (Number.isFinite(min) && largo < min) return false;
    if (Number.isFinite(max) && largo > max) return false;
    return true;
}

/* ---------- Validación de un campo del DOM ---------- */

function leerEntero(campo, atributo) {
    const valor = campo.getAttribute(atributo);
    return valor === null ? NaN : Number.parseInt(valor, 10);
}

/**
 * Valida un <input>, <select> o <textarea> según sus atributos.
 * @returns {string} mensaje de error, o cadena vacía si es válido.
 */
export function validarCampo(campo) {
    const valor = campo.value.trim();
    const tipo = (campo.type || '').toLowerCase();

    // 1. Obligatorio
    if (campo.required && esVacio(valor)) {
        return campo.tagName === 'SELECT'
            ? 'Seleccioná una opción.'
            : 'Este campo es obligatorio.';
    }

    // Si es opcional y está vacío, no hay nada más que revisar
    if (esVacio(valor)) return '';

    // 2. Formato según el tipo
    if (tipo === 'email' && !esEmailValido(valor)) {
        return 'Ingresá un correo válido, por ejemplo nombre@correo.com.';
    }

    if (tipo === 'tel' && !esTelefonoValido(valor)) {
        return 'El teléfono debe tener 8 dígitos, por ejemplo 8888-8888.';
    }

    // 3. Longitud mínima / máxima
    const min = leerEntero(campo, 'minlength');
    const max = leerEntero(campo, 'maxlength');
    if (!cumpleLongitud(valor, min, max)) {
        if (Number.isFinite(min) && valor.length < min) {
            return `Debe tener al menos ${min} caracteres (llevás ${valor.length}).`;
        }
        return `Debe tener como máximo ${max} caracteres (llevás ${valor.length}).`;
    }

    // 4. Patrón (pattern) — se usa el title del campo como mensaje
    const patron = campo.getAttribute('pattern');
    if (patron && tipo !== 'tel' && !new RegExp(`^(?:${patron})$`).test(valor)) {
        return campo.title || 'El formato ingresado no es válido.';
    }

    // 5. Rangos para números y fechas (min / max)
    if (tipo === 'number') {
        const numero = Number(valor);
        if (!Number.isInteger(numero)) return 'Ingresá un número entero.';
        if (campo.min !== '' && numero < Number(campo.min)) return `El valor mínimo es ${campo.min}.`;
        if (campo.max !== '' && numero > Number(campo.max)) return `El valor máximo es ${campo.max}.`;
    }

    if (tipo === 'date') {
        // Las fechas ISO (AAAA-MM-DD) se pueden comparar como texto
        if (campo.min && valor < campo.min) return `La fecha no puede ser anterior a ${campo.min}.`;
        if (campo.max && valor > campo.max) return `La fecha no puede ser posterior a ${campo.max}.`;
    }

    return '';
}

/* ---------- Retroalimentación visual ---------- */

function obtenerContenedor(campo) {
    return campo.closest('.campo-formulario') || campo.parentElement;
}

function obtenerNodoMensaje(campo) {
    const idMensaje = `${campo.id}-error`;
    let nodo = document.getElementById(idMensaje);

    if (!nodo) {
        // Se crea el nodo del mensaje dinámicamente la primera vez
        nodo = document.createElement('p');
        nodo.id = idMensaje;
        nodo.className = 'mensaje-campo';
        nodo.setAttribute('aria-live', 'polite');
        obtenerContenedor(campo).appendChild(nodo);

        const descritoPor = campo.getAttribute('aria-describedby');
        campo.setAttribute('aria-describedby', descritoPor ? `${descritoPor} ${idMensaje}` : idMensaje);
    }
    return nodo;
}

/**
 * Pinta el campo como válido o inválido y muestra el mensaje.
 */
export function mostrarEstadoCampo(campo, mensaje) {
    const contenedor = obtenerContenedor(campo);
    const nodo = obtenerNodoMensaje(campo);

    if (mensaje) {
        contenedor.classList.add(CLASE_ERROR);
        contenedor.classList.remove(CLASE_VALIDO);
        campo.setAttribute('aria-invalid', 'true');
        nodo.textContent = mensaje;
    } else {
        contenedor.classList.remove(CLASE_ERROR);
        // Solo se marca en verde si el campo tiene algo escrito
        contenedor.classList.toggle(CLASE_VALIDO, !esVacio(campo.value));
        campo.removeAttribute('aria-invalid');
        nodo.textContent = '';
    }
}

export function limpiarEstadoCampo(campo) {
    const contenedor = obtenerContenedor(campo);
    contenedor.classList.remove(CLASE_ERROR, CLASE_VALIDO);
    campo.removeAttribute('aria-invalid');
    const nodo = document.getElementById(`${campo.id}-error`);
    if (nodo) nodo.textContent = '';
}

/* ---------- Contador de caracteres (textarea con maxlength) ---------- */

function activarContador(campo) {
    const max = leerEntero(campo, 'maxlength');
    if (!Number.isFinite(max)) return;

    const contador = document.createElement('span');
    contador.className = 'contador-caracteres';
    contador.setAttribute('aria-hidden', 'true');
    obtenerContenedor(campo).appendChild(contador);

    const actualizar = () => {
        const largo = campo.value.length;
        contador.textContent = `${largo} / ${max}`;
        contador.classList.toggle('contador-caracteres--limite', largo >= max * 0.9);
    };
    campo.addEventListener('input', actualizar);
    campo.addEventListener('reset-visual', actualizar);
    actualizar();
}

/* ---------- API de alto nivel para formularios ---------- */

function camposValidables(formulario) {
    return Array.from(formulario.elements).filter(
        (el) => ['INPUT', 'SELECT', 'TEXTAREA'].includes(el.tagName)
            && !['submit', 'button', 'reset', 'hidden', 'checkbox'].includes(el.type)
            && el.id
    );
}

/**
 * Valida todos los campos del formulario, muestra los errores y
 * pone el foco en el primer campo inválido.
 * @returns {boolean} true si todo es válido.
 */
export function validarFormulario(formulario) {
    let primerInvalido = null;

    for (const campo of camposValidables(formulario)) {
        const mensaje = validarCampo(campo);
        mostrarEstadoCampo(campo, mensaje);
        if (mensaje && !primerInvalido) primerInvalido = campo;
    }

    if (primerInvalido) primerInvalido.focus();
    return primerInvalido === null;
}

/**
 * Activa la validación en vivo de un formulario:
 *  - blur: valida el campo al salir de él.
 *  - input/change: si el campo ya se había tocado, revalida mientras se escribe.
 *  - focus: resalta el campo activo.
 * Desactiva las burbujas nativas del navegador (noValidate) para mostrar
 * nuestros propios mensajes; los atributos HTML5 se mantienen en el HTML.
 */
export function activarValidacion(formulario) {
    formulario.noValidate = true;

    for (const campo of camposValidables(formulario)) {
        const contenedor = obtenerContenedor(campo);

        campo.addEventListener('focus', () => contenedor.classList.add('campo-formulario--activo'));

        campo.addEventListener('blur', () => {
            contenedor.classList.remove('campo-formulario--activo');
            campo.dataset.tocado = 'true';
            mostrarEstadoCampo(campo, validarCampo(campo));
        });

        const revalidar = () => {
            if (campo.dataset.tocado === 'true') mostrarEstadoCampo(campo, validarCampo(campo));
        };
        campo.addEventListener('input', revalidar);
        campo.addEventListener('change', revalidar);

        if (campo.tagName === 'TEXTAREA') activarContador(campo);
    }

    formulario.addEventListener('reset', () => {
        // Esperar a que el navegador limpie los valores antes de quitar estilos
        setTimeout(() => {
            for (const campo of camposValidables(formulario)) {
                delete campo.dataset.tocado;
                limpiarEstadoCampo(campo);
                campo.dispatchEvent(new Event('reset-visual'));
            }
        });
    });
}
