package co.edu.uniquindio.poo.fintech.estructuras.colas;

import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Cola de prioridad implementada con un heap binario de mínimo sobre un arreglo.
 * El elemento "primero" es el que el comparador considera menor.
 */
public class ColaPrioridad<T> {

    private static final int CAPACIDAD_INICIAL = 16;

    private final Comparator<? super T> comparador;
    private Object[] heap;
    private int size;

    public ColaPrioridad(Comparator<? super T> comparador) {
        this.comparador = Objects.requireNonNull(comparador, "El comparador no puede ser nulo");
        this.heap = new Object[CAPACIDAD_INICIAL];
    }

    public void encolar(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
        heap[size] = dato;
        subir(size);
        size++;
    }

    public T desencolar() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola de prioridad está vacía");
        }
        T primero = elementoEn(0);
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        bajar(0);
        return primero;
    }

    public T verPrimero() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola de prioridad está vacía");
        }
        return elementoEn(0);
    }

    /** Elimina la primera aparición del dato. Es O(n) porque hay que buscarlo. */
    public boolean eliminar(T dato) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(heap[i], dato)) {
                heap[i] = heap[size - 1];
                heap[size - 1] = null;
                size--;
                if (i < size) {
                    subir(i);
                    bajar(i);
                }
                return true;
            }
        }
        return false;
    }

    /** Devuelve una copia ordenada de los elementos. La cola no se modifica. */
    public ListaEnlazada<T> aListaOrdenada() {
        ListaEnlazada<T> copia = new ListaEnlazada<>();
        for (int i = 0; i < size; i++) {
            copia.addLast(elementoEn(i));
        }
        return copia.ordenar(comparador);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public void clear() {
        Arrays.fill(heap, null);
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private T elementoEn(int indice) {
        return (T) heap[indice];
    }

    private int comparar(int a, int b) {
        return comparador.compare(elementoEn(a), elementoEn(b));
    }

    private void intercambiar(int a, int b) {
        Object temporal = heap[a];
        heap[a] = heap[b];
        heap[b] = temporal;
    }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (comparar(i, padre) >= 0) {
                break;
            }
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        while (true) {
            int izquierdo = 2 * i + 1;
            int derecho = izquierdo + 1;
            int menor = i;
            if (izquierdo < size && comparar(izquierdo, menor) < 0) {
                menor = izquierdo;
            }
            if (derecho < size && comparar(derecho, menor) < 0) {
                menor = derecho;
            }
            if (menor == i) {
                break;
            }
            intercambiar(i, menor);
            i = menor;
        }
    }
}
