package co.edu.uniquindio.poo.fintech.estructuras.listas;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Lista enlazada simple genérica. Guarda referencia al primero y al último nodo,
 * por eso addLast es O(1).
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> nodoPrimero;
    private Nodo<T> nodoUltimo;
    private int size;

    public void addFirst(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        Nodo<T> nuevo = new Nodo<>(dato, nodoPrimero);
        nodoPrimero = nuevo;
        if (nodoUltimo == null) {
            nodoUltimo = nuevo;
        }
        size++;
    }

    public void addLast(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        Nodo<T> nuevo = new Nodo<>(dato, null);
        if (nodoUltimo == null) {
            nodoPrimero = nuevo;
        } else {
            nodoUltimo.setSiguiente(nuevo);
        }
        nodoUltimo = nuevo;
        size++;
    }

    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        return nodoPrimero.getDato();
    }

    public T getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        return nodoUltimo.getDato();
    }

    public T get(int indice) {
        if (indice < 0 || indice >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        Nodo<T> actual = nodoPrimero;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    /** Elimina la primera aparición del dato. Devuelve true si lo encontró. */
    public boolean remove(T dato) {
        Nodo<T> anterior = null;
        Nodo<T> actual = nodoPrimero;
        while (actual != null) {
            if (Objects.equals(actual.getDato(), dato)) {
                if (anterior == null) {
                    nodoPrimero = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == nodoUltimo) {
                    nodoUltimo = anterior;
                }
                size--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        T dato = nodoPrimero.getDato();
        nodoPrimero = nodoPrimero.getSiguiente();
        if (nodoPrimero == null) {
            nodoUltimo = null;
        }
        size--;
        return dato;
    }

    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        T dato = nodoUltimo.getDato();
        if (nodoPrimero == nodoUltimo) {
            nodoPrimero = null;
            nodoUltimo = null;
        } else {
            Nodo<T> actual = nodoPrimero;
            while (actual.getSiguiente() != nodoUltimo) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(null);
            nodoUltimo = actual;
        }
        size--;
        return dato;
    }

    /** Elimina todos los elementos que cumplan la condición. Devuelve cuántos eliminó. */
    public int removeIf(Predicate<? super T> condicion) {
        int eliminados = 0;
        Nodo<T> anterior = null;
        Nodo<T> actual = nodoPrimero;
        while (actual != null) {
            Nodo<T> siguiente = actual.getSiguiente();
            if (condicion.test(actual.getDato())) {
                if (anterior == null) {
                    nodoPrimero = siguiente;
                } else {
                    anterior.setSiguiente(siguiente);
                }
                if (actual == nodoUltimo) {
                    nodoUltimo = anterior;
                }
                size--;
                eliminados++;
            } else {
                anterior = actual;
            }
            actual = siguiente;
        }
        return eliminados;
    }

    public boolean contains(T dato) {
        return indexOf(dato) >= 0;
    }

    public int indexOf(T dato) {
        int i = 0;
        for (Nodo<T> actual = nodoPrimero; actual != null; actual = actual.getSiguiente(), i++) {
            if (Objects.equals(actual.getDato(), dato)) {
                return i;
            }
        }
        return -1;
    }

    /** Devuelve una lista nueva con los elementos que cumplen la condición. */
    public ListaEnlazada<T> filtrar(Predicate<? super T> condicion) {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        for (T dato : this) {
            if (condicion.test(dato)) {
                resultado.addLast(dato);
            }
        }
        return resultado;
    }

    /** Devuelve una lista nueva ordenada con merge sort. La original no cambia. */
    public ListaEnlazada<T> ordenar(Comparator<? super T> comparador) {
        Objects.requireNonNull(comparador, "El comparador no puede ser nulo");
        Object[] arreglo = toArray();
        mergeSort(arreglo, new Object[arreglo.length], 0, arreglo.length - 1, comparador);
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        for (Object o : arreglo) {
            @SuppressWarnings("unchecked")
            T dato = (T) o;
            resultado.addLast(dato);
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private static <E> void mergeSort(Object[] a, Object[] aux, int lo, int hi, Comparator<? super E> c) {
        if (hi <= lo) {
            return;
        }
        int mid = (lo + hi) >>> 1;
        mergeSort(a, aux, lo, mid, c);
        mergeSort(a, aux, mid + 1, hi, c);
        int i = lo;
        int j = mid + 1;
        int k = lo;
        while (i <= mid && j <= hi) {
            if (c.compare((E) a[i], (E) a[j]) <= 0) {
                aux[k++] = a[i++];
            } else {
                aux[k++] = a[j++];
            }
        }
        while (i <= mid) {
            aux[k++] = a[i++];
        }
        while (j <= hi) {
            aux[k++] = a[j++];
        }
        System.arraycopy(aux, lo, a, lo, hi - lo + 1);
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        nodoPrimero = null;
        nodoUltimo = null;
        size = 0;
    }

    private Object[] toArray() {
        Object[] arreglo = new Object[size];
        int i = 0;
        for (Nodo<T> actual = nodoPrimero; actual != null; actual = actual.getSiguiente()) {
            arreglo[i++] = actual.getDato();
        }
        return arreglo;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = nodoPrimero;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo<T> actual = nodoPrimero; actual != null; actual = actual.getSiguiente()) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }
}
