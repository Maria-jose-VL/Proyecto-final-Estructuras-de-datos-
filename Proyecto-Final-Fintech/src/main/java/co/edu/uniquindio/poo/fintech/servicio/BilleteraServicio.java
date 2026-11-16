package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.hash.TablaHash;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.BilleteraNoEncontradaException;
import co.edu.uniquindio.poo.fintech.excepciones.FintechException;
import co.edu.uniquindio.poo.fintech.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.poo.fintech.excepciones.ValorInvalidoException;
import co.edu.uniquindio.poo.fintech.modelo.Billetera;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoBilletera;

import java.math.BigDecimal;
import java.util.Optional;

/** Administra las billeteras. Están en una tabla hash por código y también en cada usuario. */
public class BilleteraServicio {

    private final TablaHash<String, Billetera> billeteras = new TablaHash<>();
    private final UsuarioServicio usuarios;
    private int contador;

    public BilleteraServicio(UsuarioServicio usuarios) {
        this.usuarios = usuarios;
    }

    public Billetera crear(String usuarioId, String nombre, TipoBilletera tipo, BigDecimal saldoInicial)
            throws FintechException {
        if (saldoInicial == null || saldoInicial.signum() < 0) {
            throw new ValorInvalidoException("el saldo inicial no puede ser negativo");
        }
        Usuario usuario = usuarios.buscar(usuarioId);
        String codigo = String.format("BIL-%04d", ++contador);
        Billetera billetera = new Billetera(codigo, nombre, tipo, usuarioId, saldoInicial);
        usuario.agregarBilletera(billetera);
        billeteras.put(codigo, billetera);
        return billetera;
    }

    public Billetera buscar(String codigo) throws BilleteraNoEncontradaException {
        return billeteras.get(codigo).orElseThrow(() -> new BilleteraNoEncontradaException(codigo));
    }

    /** Igual que buscar, pero devuelve Optional en lugar de lanzar excepción. */
    public Optional<Billetera> encontrar(String codigo) {
        return billeteras.get(codigo);
    }

    public ListaEnlazada<Billetera> listarDeUsuario(String usuarioId) throws UsuarioNoEncontradoException {
        return usuarios.buscar(usuarioId).getBilleteras().filtrar(b -> true);
    }

    public void bloquear(String codigo) throws BilleteraNoEncontradaException {
        buscar(codigo).bloquear();
    }

    public void activar(String codigo) throws BilleteraNoEncontradaException {
        buscar(codigo).activar();
    }

    public void cerrar(String codigo) throws FintechException {
        buscar(codigo).cerrar();
    }

    public BigDecimal consultarSaldo(String codigo) throws BilleteraNoEncontradaException {
        return buscar(codigo).getSaldo();
    }

    public BigDecimal consultarSaldoTotal(String usuarioId) throws UsuarioNoEncontradoException {
        return usuarios.buscar(usuarioId).calcularSaldoTotal();
    }

    /** Quita las billeteras del usuario de la tabla general. */
    public void eliminarDeUsuario(String usuarioId) throws UsuarioNoEncontradoException {
        for (Billetera billetera : usuarios.buscar(usuarioId).getBilleteras()) {
            billeteras.remove(billetera.getCodigo());
        }
    }

    public int contar() {
        return billeteras.getSize();
    }
}