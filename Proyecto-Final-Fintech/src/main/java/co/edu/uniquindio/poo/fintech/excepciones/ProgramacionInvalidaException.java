package co.edu.uniquindio.poo.fintech.excepciones;

public class ProgramacionInvalidaException extends FintechException {

    private static final long serialVersionUID = 1L;

    public ProgramacionInvalidaException(String detalle) {
        super("La operación programada no es válida: " + detalle);
    }
}
