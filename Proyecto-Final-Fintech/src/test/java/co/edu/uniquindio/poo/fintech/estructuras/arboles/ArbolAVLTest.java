package co.edu.uniquindio.poo.fintech.estructuras.arboles;

import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArbolAVLTest {

    @Test
    void mantieneAlturaLogaritmicaAlInsertarEnOrden() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>(Integer::compare);
        for (int i = 1; i <= 1000; i++) {
            arbol.insertar(i);
        }
        assertEquals(1000, arbol.getPeso());
        assertTrue(arbol.calcularAltura() <= 21, "altura excesiva: " + arbol.calcularAltura());
        assertEquals(1, arbol.minimo());
        assertEquals(1000, arbol.maximo());
    }

    @Test
    void inordenDevuelveLosDatosOrdenados() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>(Integer::compare);
        for (int d : new int[]{50, 20, 80, 10, 30, 70, 90}) {
            arbol.insertar(d);
        }
        assertEquals(List.of(10, 20, 30, 50, 70, 80, 90), aLista(arbol.inorden()));
    }

    @Test
    void noAceptaDuplicados() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>(Integer::compare);
        assertTrue(arbol.insertar(5));
        assertFalse(arbol.insertar(5));
        assertEquals(1, arbol.getPeso());
    }

    @Test
    void eliminarMantieneElOrdenYActualizaElPeso() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>(Integer::compare);
        for (int d : new int[]{50, 20, 80, 10, 30, 70, 90}) {
            arbol.insertar(d);
        }
        assertTrue(arbol.eliminar(50));
        assertFalse(arbol.contiene(50));
        assertEquals(6, arbol.getPeso());
        assertEquals(List.of(10, 20, 30, 70, 80, 90), aLista(arbol.inorden()));
    }

    @Test
    void obtenerMayoresDevuelveLosNMasGrandesDeMayorAMenor() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>(Integer::compare);
        for (int d : new int[]{5, 1, 9, 3, 7}) {
            arbol.insertar(d);
        }
        assertEquals(List.of(9, 7, 5), aLista(arbol.obtenerMayores(3)));
    }

    private static List<Integer> aLista(ListaEnlazada<Integer> lista) {
        List<Integer> salida = new ArrayList<>();
        for (Integer valor : lista) {
            salida.add(valor);
        }
        return salida;
    }
}