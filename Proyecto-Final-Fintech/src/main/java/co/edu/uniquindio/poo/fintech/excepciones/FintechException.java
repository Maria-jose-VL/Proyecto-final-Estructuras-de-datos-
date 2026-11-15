package co.edu.uniquindio.poo.fintech.excepciones;

/** Excepción base del sistema. Es checked: quien la usa debe atraparla. */
public class FintechException extends Exception {

    private static final long serialVersionUID = 1L;

    public FintechException(String mensaje) {
        super(mensaje);
    }
}
