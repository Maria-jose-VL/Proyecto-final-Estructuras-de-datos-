package co.edu.uniquindio.poo.fintech.estructuras.colas;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Cola FIFO genérica. Puede tener límite de capacidad: si se llena,
 * encolar lanza ColaLlena, o encolarDescartandoAntiguo saca el más viejo.
 */
public class Cola<T> implements Iterable<T> {

    private static final int SIN_LIMITE = Integer.MAX_VALUE;

    private final int limite;
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int size;

    public Cola() {
        this(SIN_LIMITE);
    }

    public Cola(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser positivo");
        }
        this.limite = limite;
    }

    public void encolar(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        if (size >= limite) {
            throw new ColaLlena("La cola llegó a su límite de " + limite + " elementos");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        size++;
    }

    /** Si la cola está llena saca el elemento más antiguo antes de encolar. */
    public void encolarDescartandoAntiguo(T dato) {
        if (size >= limite) {
            desencolar();
        }
        encolar(dato);
    }

    public T desencolar() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola está vacía");
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        size--;
        return dato;
    }

    public T verFrente() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola está vacía");
        }
        return frente.getDato();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public void clear() {
        frente = null;
        fin = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

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
