package co.edu.uniquindio.poo.fintech.estructuras.pilas;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Pila LIFO genérica implementada con nodos enlazados. */
public class Pila<T> implements Iterable<T> {

    private Nodo<T> cima;
    private int size;

    public void push(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        cima = new Nodo<>(dato, cima);
        size++;
    }

    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("La pila está vacía");
        }
        T dato = cima.getDato();
        cima = cima.getSiguiente();
        size--;
        return dato;
    }

    /** Mira el elemento de la cima sin sacarlo. */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La pila está vacía");
        }
        return cima.getDato();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public void limpiar() {
        cima = null;
        size = 0;
    }

    /** Recorre de la cima hacia la base. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cima;

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
}
