/**
 * PATRÓN ITERATOR — Coleccion
 *
 * Envuelve una lista de entidades (cursos, clientes, ...) y permite recorrerla con for...of
 * sin exponer cómo se guarda por dentro. Implementa el protocolo de iteración de JavaScript:
 * el método [Symbol.iterator]() devuelve un iterador que entrega un elemento a la vez.
 *
 * Uso:
 *   const cursos = new Coleccion(listaDeCursos);
 *   for (const curso of cursos) { ... }          // recorre con el iterador
 *   cursos.filtrar((c) => c.tieneCupos())        // nueva Coleccion, también iterable
 */
export default class Coleccion {
  #elementos;

  constructor(elementos = []) {
    this.#elementos = [...elementos];
  }

  /** Iterador: entrega los elementos uno por uno, en orden. */
  *[Symbol.iterator]() {
    for (let indice = 0; indice < this.#elementos.length; indice += 1) {
      yield this.#elementos[indice];
    }
  }

  get tamano() {
    return this.#elementos.length;
  }

  estaVacia() {
    return this.#elementos.length === 0;
  }

  /** Devuelve una nueva Coleccion solo con los elementos que cumplen la condición. */
  filtrar(condicion) {
    const resultado = [];
    for (const elemento of this) {
      if (condicion(elemento)) resultado.push(elemento);
    }
    return new Coleccion(resultado);
  }

  /** Transforma cada elemento (por ejemplo, en un componente de React) recorriendo con el iterador. */
  mapear(transformar) {
    const resultado = [];
    for (const elemento of this) {
      resultado.push(transformar(elemento));
    }
    return resultado;
  }

  /** Suma un valor numérico de cada elemento. */
  sumar(obtenerValor) {
    let total = 0;
    for (const elemento of this) {
      total += Number(obtenerValor(elemento)) || 0;
    }
    return total;
  }
}
