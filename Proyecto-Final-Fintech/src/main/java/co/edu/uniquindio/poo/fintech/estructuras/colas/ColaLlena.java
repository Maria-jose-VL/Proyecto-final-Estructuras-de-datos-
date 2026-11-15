package co.edu.uniquindio.poo.fintech.estructuras.colas;

/** Se lanza al encolar en una cola que ya alcanzó su límite. */
public class ColaLlena extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ColaLlena(String mensaje) {
        super(mensaje);
    }
}
