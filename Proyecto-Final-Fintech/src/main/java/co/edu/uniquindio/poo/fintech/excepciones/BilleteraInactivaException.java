package co.edu.uniquindio.poo.fintech.excepciones;

public class BilleteraInactivaException extends FintechException {

    private static final long serialVersionUID = 1L;

    public BilleteraInactivaException(String codigo, String estado) {
        super("La billetera " + codigo + " está " + estado.toLowerCase() + " y no admite operaciones");
    }
}
