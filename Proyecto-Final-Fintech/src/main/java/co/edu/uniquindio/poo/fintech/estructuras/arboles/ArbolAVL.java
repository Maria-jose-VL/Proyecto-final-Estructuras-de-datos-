package co.edu.uniquindio.poo.fintech.estructuras.arboles;

import java.util.Comparator;

/**
 * Árbol AVL: árbol binario de búsqueda que se rebalancea después de cada inserción
 * o eliminación. El factor de equilibrio de cada nodo queda entre -1 y 1.
 * Reescribe los métodos protegidos de ArbolBinario para aplicar el balanceo.
 */
public class ArbolAVL<T> extends ArbolBinario<T> {

    public ArbolAVL(Comparator<? super T> comparador) {
        super(comparador);
    }

    @Override
    protected NodoArbol<T> insertarRec(NodoArbol<T> nodo, T dato) {
        return balancear(super.insertarRec(nodo, dato));
    }

    @Override
    protected NodoArbol<T> eliminarRec(NodoArbol<T> nodo, T dato) {
        NodoArbol<T> resultado = super.eliminarRec(nodo, dato);
        return resultado == null ? null : balancear(resultado);
    }

    @Override
    protected NodoArbol<T> eliminarMinimo(NodoArbol<T> nodo) {
        NodoArbol<T> resultado = super.eliminarMinimo(nodo);
        return resultado == null ? null : balancear(resultado);
    }

    private int alturaDe(NodoArbol<T> nodo) {
        return nodo == null ? 0 : nodo.getAltura();
    }

    private void actualizarAltura(NodoArbol<T> nodo) {
        nodo.setAltura(1 + Math.max(alturaDe(nodo.getIzquierdo()), alturaDe(nodo.getDerecho())));
    }

    /** Altura izquierda menos altura derecha. */
    private int factorEquilibrio(NodoArbol<T> nodo) {
        return nodo == null ? 0 : alturaDe(nodo.getIzquierdo()) - alturaDe(nodo.getDerecho());
    }

    private NodoArbol<T> balancear(NodoArbol<T> nodo) {
        actualizarAltura(nodo);
        int fe = factorEquilibrio(nodo);
        if (fe > 1) {
            if (factorEquilibrio(nodo.getIzquierdo()) < 0) {
                return rotacionDobleIzqDer(nodo);
            }
            return rotacionSimpleDerecha(nodo);
        }
        if (fe < -1) {
            if (factorEquilibrio(nodo.getDerecho()) > 0) {
                return rotacionDobleDerIzq(nodo);
            }
            return rotacionSimpleIzquierda(nodo);
        }
        return nodo;
    }

    private NodoArbol<T> rotacionSimpleDerecha(NodoArbol<T> y) {
        NodoArbol<T> x = y.getIzquierdo();
        y.setIzquierdo(x.getDerecho());
        x.setDerecho(y);
        actualizarAltura(y);
        actualizarAltura(x);
        return x;
    }

    private NodoArbol<T> rotacionSimpleIzquierda(NodoArbol<T> x) {
        NodoArbol<T> y = x.getDerecho();
        x.setDerecho(y.getIzquierdo());
        y.setIzquierdo(x);
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    /** Caso izquierda-derecha: primero rota el hijo izquierdo a la izquierda. */
    private NodoArbol<T> rotacionDobleIzqDer(NodoArbol<T> nodo) {
        nodo.setIzquierdo(rotacionSimpleIzquierda(nodo.getIzquierdo()));
        return rotacionSimpleDerecha(nodo);
    }

    /** Caso derecha-izquierda: primero rota el hijo derecho a la derecha. */
    private NodoArbol<T> rotacionDobleDerIzq(NodoArbol<T> nodo) {
        nodo.setDerecho(rotacionSimpleDerecha(nodo.getDerecho()));
        return rotacionSimpleIzquierda(nodo);
    }
}
