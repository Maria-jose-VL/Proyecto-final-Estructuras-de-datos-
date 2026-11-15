package co.edu.uniquindio.poo.fintech.excepciones;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends FintechException {

    private static final long serialVersionUID = 1L;

    private final BigDecimal saldoDisponible;
    private final BigDecimal valorSolicitado;

    public SaldoInsuficienteException(BigDecimal saldoDisponible, BigDecimal valorSolicitado) {
        super("Saldo insuficiente: disponible " + saldoDisponible + ", solicitado " + valorSolicitado);
        this.saldoDisponible = saldoDisponible;
        this.valorSolicitado = valorSolicitado;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }
}
