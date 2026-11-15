package co.edu.uniquindio.poo.fintech.excepciones;

public class UsuarioDuplicadoException extends FintechException {

    private static final long serialVersionUID = 1L;

    public UsuarioDuplicadoException(String id) {
        super("Ya existe un usuario con documento " + id);
    }
}
