package co.edu.uniquindio.poo.fintech.estructuras.arboles;

import co.edu.uniquindio.poo.fintech.estructuras.colas.Cola;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Árbol binario de búsqueda genérico. No acepta dos elementos "iguales" según el comparador:
 * si el dato ya existe, insertar devuelve false y no cambia nada.
 * Los métodos protegidos existen para que ArbolAVL pueda reescribirlos con balanceo.
 */
public class ArbolBinario<T> {

    protected NodoArbol<T> raiz;
    protected int peso;
    protected final Comparator<? super T> comparador;

    public ArbolBinario(Comparator<? super T> comparador) {
        this.comparador = Objects.requireNonNull(comparador, "El comparador no puede ser nulo");
    }

    public boolean insertar(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        int antes = peso;
        raiz = insertarRec(raiz, dato);
        return peso > antes;
    }

    public boolean eliminar(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        int antes = peso;
        raiz = eliminarRec(raiz, dato);
        return peso < antes;
    }

    public Optional<T> buscar(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        NodoArbol<T> actual = raiz;
        while (actual != null) {
            int cmp = comparador.compare(dato, actual.getElemento());
            if (cmp == 0) {
                return Optional.of(actual.getElemento());
            }
            actual = cmp < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return Optional.empty();
    }

    public boolean contiene(T dato) {
        return buscar(dato).isPresent();
    }

    public T minimo() {
        if (estaVacio()) {
            throw new NoSuchElementException("El árbol está vacío");
        }
        NodoArbol<T> actual = raiz;
        while (actual.getIzquierdo() != null) {
            actual = actual.getIzquierdo();
        }
        return actual.getElemento();
    }

    public T maximo() {
        if (estaVacio()) {
            throw new NoSuchElementException("El árbol está vacío");
        }
        NodoArbol<T> actual = raiz;
        while (actual.getDerecho() != null) {
            actual = actual.getDerecho();
        }
        return actual.getElemento();
    }

    public ListaEnlazada<T> inorden() {
        ListaEnlazada<T> salida = new ListaEnlazada<>();
        inordenRec(raiz, salida);
        return salida;
    }

    public ListaEnlazada<T> preorden() {
        ListaEnlazada<T> salida = new ListaEnlazada<>();
        preordenRec(raiz, salida);
        return salida;
    }

    public ListaEnlazada<T> postorden() {
        ListaEnlazada<T> salida = new ListaEnlazada<>();
        postordenRec(raiz, salida);
        return salida;
    }

    /** Recorrido por niveles, usando una cola. */
    public ListaEnlazada<T> recorridoPorNiveles() {
        ListaEnlazada<T> salida = new ListaEnlazada<>();
        if (raiz == null) {
            return salida;
        }
        Cola<NodoArbol<T>> cola = new Cola<>();
        cola.encolar(raiz);
        while (!cola.isEmpty()) {
            NodoArbol<T> nodo = cola.desencolar();
            salida.addLast(nodo.getElemento());
            if (nodo.getIzquierdo() != null) {
                cola.encolar(nodo.getIzquierdo());
            }
            if (nodo.getDerecho() != null) {
                cola.encolar(nodo.getDerecho());
            }
        }
        return salida;
    }

    /** Elementos entre min y max, ambos incluidos, en orden. */
    public ListaEnlazada<T> buscarEnRango(T min, T max) {
        Objects.requireNonNull(min);
        Objects.requireNonNull(max);
        ListaEnlazada<T> salida = new ListaEnlazada<>();
        rangoRec(raiz, min, max, salida);
        return salida;
    }

    /** Los n elementos más grandes, del mayor al menor. */
    public ListaEnlazada<T> obtenerMayores(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n no puede ser negativo");
        }
        ListaEnlazada<T> salida = new ListaEnlazada<>();
        mayoresRec(raiz, n, salida);
        return salida;
    }

    public int getPeso() {
        return peso;
    }

    public int calcularAltura() {
        return alturaRec(raiz);
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public void limpiar() {
        raiz = null;
        peso = 0;
    }

    protected NodoArbol<T> insertarRec(NodoArbol<T> nodo, T dato) {
        if (nodo == null) {
            peso++;
            return new NodoArbol<>(dato);
        }
        int cmp = comparador.compare(dato, nodo.getElemento());
        if (cmp < 0) {
            nodo.setIzquierdo(insertarRec(nodo.getIzquierdo(), dato));
        } else if (cmp > 0) {
            nodo.setDerecho(insertarRec(nodo.getDerecho(), dato));
        }
        return nodo;
    }

    protected NodoArbol<T> eliminarRec(NodoArbol<T> nodo, T dato) {
        if (nodo == null) {
            return null;
        }
        int cmp = comparador.compare(dato, nodo.getElemento());
        if (cmp < 0) {
            nodo.setIzquierdo(eliminarRec(nodo.getIzquierdo(), dato));
        } else if (cmp > 0) {
            nodo.setDerecho(eliminarRec(nodo.getDerecho(), dato));
        } else {
            peso--;
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            // Dos hijos: se reemplaza por el sucesor (mínimo del subárbol derecho).
            NodoArbol<T> sucesor = nodo.getDerecho();
            while (sucesor.getIzquierdo() != null) {
                sucesor = sucesor.getIzquierdo();
            }
            nodo.setElemento(sucesor.getElemento());
            nodo.setDerecho(eliminarMinimo(nodo.getDerecho()));
        }
        return nodo;
    }

    /** Quita el nodo mínimo del subárbol sin cambiar el peso (el peso ya se ajustó). */
    protected NodoArbol<T> eliminarMinimo(NodoArbol<T> nodo) {
        if (nodo.getIzquierdo() == null) {
            return nodo.getDerecho();
        }
        nodo.setIzquierdo(eliminarMinimo(nodo.getIzquierdo()));
        return nodo;
    }

    private void inordenRec(NodoArbol<T> nodo, ListaEnlazada<T> salida) {
        if (nodo == null) {
            return;
        }
        inordenRec(nodo.getIzquierdo(), salida);
        salida.addLast(nodo.getElemento());
        inordenRec(nodo.getDerecho(), salida);
    }

    private void preordenRec(NodoArbol<T> nodo, ListaEnlazada<T> salida) {
        if (nodo == null) {
            return;
        }
        salida.addLast(nodo.getElemento());
        preordenRec(nodo.getIzquierdo(), salida);
        preordenRec(nodo.getDerecho(), salida);
    }

    private void postordenRec(NodoArbol<T> nodo, ListaEnlazada<T> salida) {
        if (nodo == null) {
            return;
        }
        postordenRec(nodo.getIzquierdo(), salida);
        postordenRec(nodo.getDerecho(), salida);
        salida.addLast(nodo.getElemento());
    }

    private void rangoRec(NodoArbol<T> nodo, T min, T max, ListaEnlazada<T> salida) {
        if (nodo == null) {
            return;
        }
        if (comparador.compare(nodo.getElemento(), min) > 0) {
            rangoRec(nodo.getIzquierdo(), min, max, salida);
        }
        if (comparador.compare(nodo.getElemento(), min) >= 0
                && comparador.compare(nodo.getElemento(), max) <= 0) {
            salida.addLast(nodo.getElemento());
        }
        if (comparador.compare(nodo.getElemento(), max) < 0) {
            rangoRec(nodo.getDerecho(), min, max, salida);
        }
    }

    private void mayoresRec(NodoArbol<T> nodo, int n, ListaEnlazada<T> salida) {
        if (nodo == null || salida.getSize() >= n) {
            return;
        }
        mayoresRec(nodo.getDerecho(), n, salida);
        if (salida.getSize() < n) {
            salida.addLast(nodo.getElemento());
        }
        mayoresRec(nodo.getIzquierdo(), n, salida);
    }

    private int alturaRec(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(alturaRec(nodo.getIzquierdo()), alturaRec(nodo.getDerecho()));
    }
}
