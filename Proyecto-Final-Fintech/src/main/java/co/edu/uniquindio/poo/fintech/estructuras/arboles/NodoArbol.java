package co.edu.uniquindio.poo.fintech.estructuras.arboles;

/** Nodo de un árbol binario. La altura solo la usa el AVL. */
public class NodoArbol<T> {
    private T elemento;
    private NodoArbol<T> izquierdo;
    private NodoArbol<T> derecho;
    private int altura = 1;

    public NodoArbol(T elemento) {
        this.elemento = elemento;
    }

    public T getElemento() {
        return elemento;
    }

    public void setElemento(T elemento) {
        this.elemento = elemento;
    }

    public NodoArbol<T> getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoArbol<T> izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoArbol<T> getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoArbol<T> derecho) {
        this.derecho = derecho;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }
}
