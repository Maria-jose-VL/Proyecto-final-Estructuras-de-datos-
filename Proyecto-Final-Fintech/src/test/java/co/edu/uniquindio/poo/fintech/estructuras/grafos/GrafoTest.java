package co.edu.uniquindio.poo.fintech.estructuras.grafos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrafoTest {

    @Test
    void recorridoBfsVisitaPorNiveles() {
        Grafo<String> g = new Grafo<>();
        g.agregarVertice("A");
        g.agregarVertice("B");
        g.agregarVertice("C");
        g.agregarVertice("D");
        g.agregarArista("A", "B");
        g.agregarArista("A", "C");
        g.agregarArista("B", "D");
        assertEquals(4, g.recorridoBFS("A").getSize());
        assertEquals("A", g.recorridoBFS("A").getFirst());
        assertEquals("D", g.recorridoBFS("A").getLast());
    }

    @Test
    void recorridoDfsAlcanzaTodosLosVerticesAlcanzables() {
        Grafo<String> g = new Grafo<>();
        g.agregarVertice("A");
        g.agregarVertice("B");
        g.agregarVertice("C");
        g.agregarArista("A", "B");
        g.agregarArista("B", "C");
        assertEquals(3, g.recorridoDFS("A").getSize());
        assertTrue(g.existeCamino("A", "C"));
        assertFalse(g.existeCamino("C", "A"));
    }

    @Test
    void detectaCiclos() {
        Grafo<String> ciclico = new Grafo<>();
        ciclico.agregarVertice("A");
        ciclico.agregarVertice("B");
        ciclico.agregarArista("A", "B");
        ciclico.agregarArista("B", "A");
        assertTrue(ciclico.tieneCiclos());

        Grafo<String> aciclico = new Grafo<>();
        aciclico.agregarVertice("A");
        aciclico.agregarVertice("B");
        aciclico.agregarVertice("C");
        aciclico.agregarArista("A", "B");
        aciclico.agregarArista("B", "C");
        assertFalse(aciclico.tieneCiclos());
    }

    @Test
    void aristaRepetidaSumaPesoYDisminuirLaElimina() {
        Grafo<String> g = new Grafo<>();
        g.agregarVertice("A");
        g.agregarVertice("B");
        g.agregarArista("A", "B");
        g.agregarArista("A", "B");
        assertEquals(2, g.getPeso("A", "B"));
        assertTrue(g.disminuirPeso("A", "B"));
        assertEquals(1, g.getPeso("A", "B"));
        assertTrue(g.disminuirPeso("A", "B"));
        assertFalse(g.existeArista("A", "B"));
    }

    @Test
    void aristasMasFrecuentesOrdenaPorPeso() {
        Grafo<String> g = new Grafo<>();
        g.agregarVertice("A");
        g.agregarVertice("B");
        g.agregarVertice("C");
        g.agregarArista("A", "B");
        g.agregarArista("A", "B");
        g.agregarArista("A", "B");
        g.agregarArista("A", "C");
        Grafo.Arco<String> top = g.aristasMasFrecuentes(1).getFirst();
        assertEquals("B", top.destino());
        assertEquals(3, top.peso());
    }
}
