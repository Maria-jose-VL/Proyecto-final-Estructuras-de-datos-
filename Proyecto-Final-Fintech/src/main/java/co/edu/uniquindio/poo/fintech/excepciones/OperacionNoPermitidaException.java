package co.edu.uniquindio.poo.fintech.excepciones;

public class OperacionNoPermitidaException extends FintechException {

    private static final long serialVersionUID = 1L;

    public OperacionNoPermitidaException(String detalle) {
        super("Operación no permitida: " + detalle);
    }
}
