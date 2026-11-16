package co.edu.uniquindio.poo.fintech.modelo;

import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.poo.fintech.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.poo.fintech.excepciones.ValorInvalidoException;
import co.edu.uniquindio.poo.fintech.modelo.enums.EstadoBilletera;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoBilletera;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Cuenta de dinero de un usuario. El saldo siempre tiene dos decimales. */
public class Billetera {

    private final String codigo;
    private String nombre;
    private final TipoBilletera tipo;
    private BigDecimal saldo;
    private EstadoBilletera estado;
    private final String usuarioId;
    private final ListaEnlazada<Transaccion> historial = new ListaEnlazada<>();

    public Billetera(String codigo, String nombre, TipoBilletera tipo, String usuarioId, BigDecimal saldoInicial) {
        this.codigo = Objects.requireNonNull(codigo);
        this.nombre = Objects.requireNonNull(nombre);
        this.tipo = Objects.requireNonNull(tipo);
        this.usuarioId = Objects.requireNonNull(usuarioId);
        this.saldo = saldoInicial.setScale(2, RoundingMode.HALF_UP);
        this.estado = EstadoBilletera.ACTIVA;
    }

    public void depositar(BigDecimal valor) throws ValorInvalidoException {
        validarValor(valor);
        saldo = saldo.add(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public void retirar(BigDecimal valor) throws ValorInvalidoException, SaldoInsuficienteException {
        validarValor(valor);
        if (!tieneSaldo(valor)) {
            throw new SaldoInsuficienteException(saldo, valor);
        }
        saldo = saldo.subtract(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean tieneSaldo(BigDecimal valor) {
        return saldo.compareTo(valor) >= 0;
    }

    public boolean estaActiva() {
        return estado == EstadoBilletera.ACTIVA;
    }

    public void bloquear() {
        estado = EstadoBilletera.BLOQUEADA;
    }

    public void activar() {
        estado = EstadoBilletera.ACTIVA;
    }

    /** Solo se puede cerrar con saldo cero. */
    public void cerrar() throws OperacionNoPermitidaException {
        if (saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new OperacionNoPermitidaException("la billetera " + codigo + " debe quedar en cero para cerrarla");
        }
        estado = EstadoBilletera.CERRADA;
    }

    public void registrarTransaccion(Transaccion transaccion) {
        historial.addLast(transaccion);
    }

    public ListaEnlazada<Transaccion> getHistorial() {
        return historial;
    }

    public int getCantidadMovimientos() {
        return historial.getSize();
    }

    private static void validarValor(BigDecimal valor) throws ValorInvalidoException {
        if (valor == null || valor.signum() <= 0) {
            throw new ValorInvalidoException("el valor debe ser mayor que cero");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre);
    }

    public TipoBilletera getTipo() {
        return tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public EstadoBilletera getEstado() {
        return estado;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Billetera b && codigo.equals(b.codigo));
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return nombre + " [" + codigo + "] " + saldo + " " + estado;
    }
}
