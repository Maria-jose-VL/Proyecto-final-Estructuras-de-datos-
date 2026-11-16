package co.edu.uniquindio.poo.fintech.excepciones;

public class PuntosInsuficientesException extends FintechException {

    private static final long serialVersionUID = 1L;

    public PuntosInsuficientesException(String detalle) {
        super("No puedes canjear este beneficio: " + detalle);
    }
}
