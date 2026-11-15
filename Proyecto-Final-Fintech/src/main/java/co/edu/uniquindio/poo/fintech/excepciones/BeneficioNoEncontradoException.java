package co.edu.uniquindio.poo.fintech.excepciones;

public class BeneficioNoEncontradoException extends FintechException {

    private static final long serialVersionUID = 1L;

    public BeneficioNoEncontradoException(String id) {
        super("No existe un beneficio con código " + id);
    }
}
