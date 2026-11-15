package co.edu.uniquindio.poo.fintech.estructuras.hash;

import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;

import java.util.Objects;
import java.util.Optional;

/**
 * Tabla hash con encadenamiento por casillas. Cuando la carga pasa de 0,75
 * duplica la capacidad y reacomoda todas las entradas.
 */
public class TablaHash<K, V> {

    private static final int CAPACIDAD_INICIAL = 16;
    private static final double FACTOR_CARGA = 0.75;

    private NodoHash<K, V>[] casillas;
    private int size;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public TablaHash() {
        casillas = (NodoHash<K, V>[]) new NodoHash[CAPACIDAD_INICIAL];
    }

    /** Inserta la clave o, si ya existe, actualiza su valor. */
    public void put(K clave, V valor) {
        Objects.requireNonNull(clave, "La clave no puede ser nula");
        Objects.requireNonNull(valor, "El valor no puede ser nulo");
        int i = indice(clave, casillas.length);
        for (NodoHash<K, V> nodo = casillas[i]; nodo != null; nodo = nodo.getSiguiente()) {
            if (nodo.getClave().equals(clave)) {
                nodo.setValor(valor);
                return;
            }
        }
        casillas[i] = new NodoHash<>(clave, valor, casillas[i]);
        size++;
        if ((double) size / casillas.length > FACTOR_CARGA) {
            redimensionar();
        }
    }

    public Optional<V> get(K clave) {
        NodoHash<K, V> nodo = buscarNodo(clave);
        return nodo == null ? Optional.empty() : Optional.of(nodo.getValor());
    }

    public boolean containsKey(K clave) {
        return buscarNodo(clave) != null;
    }

    /** Elimina la clave. Devuelve true si existía. */
    public boolean remove(K clave) {
        Objects.requireNonNull(clave, "La clave no puede ser nula");
        int i = indice(clave, casillas.length);
        NodoHash<K, V> anterior = null;
        for (NodoHash<K, V> nodo = casillas[i]; nodo != null; nodo = nodo.getSiguiente()) {
            if (nodo.getClave().equals(clave)) {
                if (anterior == null) {
                    casillas[i] = nodo.getSiguiente();
                } else {
                    anterior.setSiguiente(nodo.getSiguiente());
                }
                size--;
                return true;
            }
            anterior = nodo;
        }
        return false;
    }

    public ListaEnlazada<K> keys() {
        ListaEnlazada<K> claves = new ListaEnlazada<>();
        for (NodoHash<K, V> casilla : casillas) {
            for (NodoHash<K, V> nodo = casilla; nodo != null; nodo = nodo.getSiguiente()) {
                claves.addLast(nodo.getClave());
            }
        }
        return claves;
    }

    public ListaEnlazada<V> values() {
        ListaEnlazada<V> valores = new ListaEnlazada<>();
        for (NodoHash<K, V> casilla : casillas) {
            for (NodoHash<K, V> nodo = casilla; nodo != null; nodo = nodo.getSiguiente()) {
                valores.addLast(nodo.getValor());
            }
        }
        return valores;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void clear() {
        casillas = (NodoHash<K, V>[]) new NodoHash[CAPACIDAD_INICIAL];
        size = 0;
    }

    private NodoHash<K, V> buscarNodo(K clave) {
        Objects.requireNonNull(clave, "La clave no puede ser nula");
        for (NodoHash<K, V> nodo = casillas[indice(clave, casillas.length)];
             nodo != null; nodo = nodo.getSiguiente()) {
            if (nodo.getClave().equals(clave)) {
                return nodo;
            }
        }
        return null;
    }

    private static int indice(Object clave, int capacidad) {
        return (clave.hashCode() & 0x7fffffff) % capacidad;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void redimensionar() {
        NodoHash<K, V>[] anteriores = casillas;
        casillas = (NodoHash<K, V>[]) new NodoHash[anteriores.length * 2];
        for (NodoHash<K, V> casilla : anteriores) {
            NodoHash<K, V> nodo = casilla;
            while (nodo != null) {
                NodoHash<K, V> siguiente = nodo.getSiguiente();
                int i = indice(nodo.getClave(), casillas.length);
                nodo.setSiguiente(casillas[i]);
                casillas[i] = nodo;
                nodo = siguiente;
            }
        }
    }
}
