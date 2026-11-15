package co.edu.uniquindio.poo.fintech.estructuras.grafos;

/**
 * Arista dirigida hacia un destino. El peso cuenta cuántas veces se repite la conexión
 * (por ejemplo, cuántas transferencias hubo entre dos usuarios).
 */
public class Arista<T> {
    private final T destino;
    private int peso;

    public Arista(T destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public T getDestino() {
        return destino;
    }

    public int getPeso() {
        return peso;
    }

    public void incrementar() {
        peso++;
    }

    public void decrementar() {
        peso--;
    }
}
