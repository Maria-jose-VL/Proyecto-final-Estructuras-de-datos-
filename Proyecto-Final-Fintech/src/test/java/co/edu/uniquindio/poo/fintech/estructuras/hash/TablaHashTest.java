package co.edu.uniquindio.poo.fintech.estructuras.hash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TablaHashTest {

    @Test
    void putGetYActualizacion() {
        TablaHash<String, Integer> tabla = new TablaHash<>();
        tabla.put("a", 1);
        tabla.put("a", 2);
        assertEquals(1, tabla.getSize());
        assertEquals(2, tabla.get("a").orElseThrow());
        assertTrue(tabla.get("zz").isEmpty());
    }

    @Test
    void removeQuitaSoloLaClaveIndicada() {
        TablaHash<String, Integer> tabla = new TablaHash<>();
        tabla.put("x", 1);
        tabla.put("y", 2);
        assertTrue(tabla.remove("x"));
        assertFalse(tabla.remove("x"));
        assertFalse(tabla.containsKey("x"));
        assertTrue(tabla.containsKey("y"));
        assertEquals(1, tabla.getSize());
    }

    @Test
    void redimensionaYConservaTodasLasEntradas() {
        TablaHash<Integer, Integer> tabla = new TablaHash<>();
        for (int i = 0; i < 1000; i++) {
            tabla.put(i, i * 2);
        }
        assertEquals(1000, tabla.getSize());
        for (int i = 0; i < 1000; i++) {
            assertEquals(i * 2, tabla.get(i).orElseThrow());
        }
        assertEquals(1000, tabla.keys().getSize());
    }
}
