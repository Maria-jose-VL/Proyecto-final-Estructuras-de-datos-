package co.edu.uniquindio.poo.fintech.excepciones;

public class BilleteraNoEncontradaException extends FintechException {

    private static final long serialVersionUID = 1L;

    public BilleteraNoEncontradaException(String codigo) {
        super("No existe una billetera con código " + codigo);
    }
}
