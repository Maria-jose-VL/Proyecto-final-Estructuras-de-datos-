package co.edu.uniquindio.poo.fintech.estructuras.grafos;

import co.edu.uniquindio.poo.fintech.estructuras.colas.Cola;
import co.edu.uniquindio.poo.fintech.estructuras.hash.TablaHash;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.estructuras.pilas.Pila;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Grafo dirigido y ponderado con lista de adyacencia. Cada vértice guarda una lista de
 * aristas salientes, y la lista de vértices vive en una tabla hash.
 */
public class Grafo<T> {

    /** Arco de origen a destino con su peso, para los reportes de frecuencia. */
    public record Arco<V>(V origen, V destino, int peso) {
    }

    private static final int NO_VISITADO = 0;
    private static final int EN_CAMINO = 1;
    private static final int VISITADO = 2;

    private final TablaHash<T, ListaEnlazada<Arista<T>>> adyacencia = new TablaHash<>();

    public void agregarVertice(T vertice) {
        Objects.requireNonNull(vertice, "El vértice no puede ser nulo");
        if (!adyacencia.containsKey(vertice)) {
            adyacencia.put(vertice, new ListaEnlazada<>());
        }
    }

    /** Elimina el vértice y todas las aristas que llegan a él. */
    public void eliminarVertice(T vertice) {
        verificarVertice(vertice);
        adyacencia.remove(vertice);
        for (T otro : adyacencia.keys()) {
            listaDe(otro).removeIf(arista -> arista.getDestino().equals(vertice));
        }
    }

    /** Agrega la arista; si ya existía, suma 1 a su peso. */
    public void agregarArista(T origen, T destino) {
        verificarVertice(origen);
        verificarVertice(destino);
        ListaEnlazada<Arista<T>> salientes = listaDe(origen);
        Optional<Arista<T>> existente = buscarArista(origen, destino);
        if (existente.isPresent()) {
            existente.get().incrementar();
        } else {
            salientes.addLast(new Arista<>(destino, 1));
        }
    }

    /** Resta 1 al peso; si llega a cero, elimina la arista. */
    public boolean disminuirPeso(T origen, T destino) {
        Optional<Arista<T>> arista = buscarArista(origen, destino);
        if (arista.isEmpty()) {
            return false;
        }
        arista.get().decrementar();
        if (arista.get().getPeso() == 0) {
            eliminarArista(origen, destino);
        }
        return true;
    }

    public boolean eliminarArista(T origen, T destino) {
        verificarVertice(origen);
        return listaDe(origen).removeIf(arista -> arista.getDestino().equals(destino)) > 0;
    }

    public boolean existeVertice(T vertice) {
        return adyacencia.containsKey(vertice);
    }

    public boolean existeArista(T origen, T destino) {
        return buscarArista(origen, destino).isPresent();
    }

    public int getPeso(T origen, T destino) {
        return buscarArista(origen, destino).map(Arista::getPeso).orElse(0);
    }

    public ListaEnlazada<T> vecinos(T vertice) {
        verificarVertice(vertice);
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        for (Arista<T> arista : listaDe(vertice)) {
            resultado.addLast(arista.getDestino());
        }
        return resultado;
    }

    public int gradoSalida(T vertice) {
        verificarVertice(vertice);
        return listaDe(vertice).getSize();
    }

    public int gradoEntrada(T vertice) {
        verificarVertice(vertice);
        int grado = 0;
        for (T otro : adyacencia.keys()) {
            for (Arista<T> arista : listaDe(otro)) {
                if (arista.getDestino().equals(vertice)) {
                    grado++;
                }
            }
        }
        return grado;
    }

    public ListaEnlazada<T> getVertices() {
        return adyacencia.keys();
    }

    /** Recorrido en anchura desde un vértice. Usa una cola. */
    public ListaEnlazada<T> recorridoBFS(T inicio) {
        verificarVertice(inicio);
        ListaEnlazada<T> orden = new ListaEnlazada<>();
        TablaHash<T, Boolean> visitados = new TablaHash<>();
        Cola<T> cola = new Cola<>();
        cola.encolar(inicio);
        visitados.put(inicio, true);
        while (!cola.isEmpty()) {
            T actual = cola.desencolar();
            orden.addLast(actual);
            for (T vecino : vecinos(actual)) {
                if (!visitados.containsKey(vecino)) {
                    visitados.put(vecino, true);
                    cola.encolar(vecino);
                }
            }
        }
        return orden;
    }

    /** Recorrido en profundidad desde un vértice. Usa una pila en vez de recursión. */
    public ListaEnlazada<T> recorridoDFS(T inicio) {
        verificarVertice(inicio);
        ListaEnlazada<T> orden = new ListaEnlazada<>();
        TablaHash<T, Boolean> visitados = new TablaHash<>();
        Pila<T> pila = new Pila<>();
        pila.push(inicio);
        while (!pila.isEmpty()) {
            T actual = pila.pop();
            if (visitados.containsKey(actual)) {
                continue;
            }
            visitados.put(actual, true);
            orden.addLast(actual);
            for (T vecino : vecinos(actual)) {
                if (!visitados.containsKey(vecino)) {
                    pila.push(vecino);
                }
            }
        }
        return orden;
    }

    public boolean existeCamino(T origen, T destino) {
        return recorridoBFS(origen).contains(destino);
    }

    public boolean tieneCiclos() {
        return !encontrarCiclos().isEmpty();
    }

    /** Devuelve cada ciclo como lista de vértices; el primero se repite al final. */
    public ListaEnlazada<ListaEnlazada<T>> encontrarCiclos() {
        ListaEnlazada<ListaEnlazada<T>> ciclos = new ListaEnlazada<>();
        TablaHash<T, Integer> estado = new TablaHash<>();
        ListaEnlazada<T> camino = new ListaEnlazada<>();
        for (T vertice : adyacencia.keys()) {
            if (estado.get(vertice).orElse(NO_VISITADO) == NO_VISITADO) {
                dfsCiclos(vertice, estado, camino, ciclos);
            }
        }
        return ciclos;
    }

    /** Las n aristas con mayor peso, de mayor a menor. */
    public ListaEnlazada<Arco<T>> aristasMasFrecuentes(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n no puede ser negativo");
        }
        ListaEnlazada<Arco<T>> todos = new ListaEnlazada<>();
        for (T origen : adyacencia.keys()) {
            for (Arista<T> arista : listaDe(origen)) {
                todos.addLast(new Arco<>(origen, arista.getDestino(), arista.getPeso()));
            }
        }
        ListaEnlazada<Arco<T>> ordenados = todos.ordenar((a, b) -> Integer.compare(b.peso(), a.peso()));
        ListaEnlazada<Arco<T>> resultado = new ListaEnlazada<>();
        for (Arco<T> arco : ordenados) {
            if (resultado.getSize() >= n) {
                break;
            }
            resultado.addLast(arco);
        }
        return resultado;
    }

    private void dfsCiclos(T vertice, TablaHash<T, Integer> estado,
                           ListaEnlazada<T> camino, ListaEnlazada<ListaEnlazada<T>> ciclos) {
        estado.put(vertice, EN_CAMINO);
        camino.addLast(vertice);
        for (Arista<T> arista : listaDe(vertice)) {
            T siguiente = arista.getDestino();
            int e = estado.get(siguiente).orElse(NO_VISITADO);
            if (e == EN_CAMINO) {
                ListaEnlazada<T> ciclo = new ListaEnlazada<>();
                for (int i = camino.indexOf(siguiente); i < camino.getSize(); i++) {
                    ciclo.addLast(camino.get(i));
                }
                ciclo.addLast(siguiente);
                ciclos.addLast(ciclo);
            } else if (e == NO_VISITADO) {
                dfsCiclos(siguiente, estado, camino, ciclos);
            }
        }
        camino.removeLast();
        estado.put(vertice, VISITADO);
    }

    private Optional<Arista<T>> buscarArista(T origen, T destino) {
        verificarVertice(origen);
        for (Arista<T> arista : listaDe(origen)) {
            if (arista.getDestino().equals(destino)) {
                return Optional.of(arista);
            }
        }
        return Optional.empty();
    }

    private ListaEnlazada<Arista<T>> listaDe(T vertice) {
        return adyacencia.get(vertice).orElseThrow(
                () -> new NoSuchElementException("El vértice no existe: " + vertice));
    }

    private void verificarVertice(T vertice) {
        Objects.requireNonNull(vertice, "El vértice no puede ser nulo");
        if (!adyacencia.containsKey(vertice)) {
            throw new NoSuchElementException("El vértice no existe: " + vertice);
        }
    }
}
