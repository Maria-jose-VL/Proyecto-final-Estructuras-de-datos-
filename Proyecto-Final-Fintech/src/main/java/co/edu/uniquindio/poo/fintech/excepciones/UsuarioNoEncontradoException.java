package co.edu.uniquindio.poo.fintech.excepciones;

public class UsuarioNoEncontradoException extends FintechException {

    private static final long serialVersionUID = 1L;

    public UsuarioNoEncontradoException(String id) {
        super("No existe un usuario con documento " + id);
    }
}
