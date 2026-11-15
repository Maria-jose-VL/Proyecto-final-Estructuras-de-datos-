package co.edu.uniquindio.poo.fintech.excepciones;

public class ValorInvalidoException extends FintechException {

    private static final long serialVersionUID = 1L;

    public ValorInvalidoException(String detalle) {
        super("Valor no válido: " + detalle);
    }
}
