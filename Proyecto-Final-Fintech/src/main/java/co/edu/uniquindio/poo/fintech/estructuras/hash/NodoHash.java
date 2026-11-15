package co.edu.uniquindio.poo.fintech.estructuras.hash;

/** Entrada de una casilla de la tabla hash, encadenada con el siguiente nodo. */
public class NodoHash<K, V> {
    private final K clave;
    private V valor;
    private NodoHash<K, V> siguiente;

    public NodoHash(K clave, V valor, NodoHash<K, V> siguiente) {
        this.clave = clave;
        this.valor = valor;
        this.siguiente = siguiente;
    }

    public K getClave() {
        return clave;
    }

    public V getValor() {
        return valor;
    }

    public void setValor(V valor) {
        this.valor = valor;
    }

    public NodoHash<K, V> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoHash<K, V> siguiente) {
        this.siguiente = siguiente;
    }
}
