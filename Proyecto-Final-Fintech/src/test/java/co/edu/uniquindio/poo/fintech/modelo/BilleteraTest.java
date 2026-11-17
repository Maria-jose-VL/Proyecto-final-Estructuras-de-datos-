package co.edu.uniquindio.poo.fintech.modelo;

import co.edu.uniquindio.poo.fintech.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.poo.fintech.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.poo.fintech.excepciones.ValorInvalidoException;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoBilletera;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BilleteraTest {

    private Billetera nueva(String saldo) {
        return new Billetera("BIL-T", "Prueba", TipoBilletera.AHORRO, "1", new BigDecimal(saldo));
    }

    @Test
    void depositarYRetirarActualizanElSaldoConDosDecimales() throws Exception {
        Billetera b = nueva("100");
        b.depositar(new BigDecimal("25.5"));
        b.retirar(new BigDecimal("0.5"));
        assertEquals(new BigDecimal("125.00"), b.getSaldo());
    }

    @Test
    void retirarMasDeLoDisponibleLanzaSaldoInsuficiente() {
        Billetera b = nueva("10");
        assertThrows(SaldoInsuficienteException.class, () -> b.retirar(new BigDecimal("10.01")));
        assertEquals(new BigDecimal("10.00"), b.getSaldo());
    }

    @Test
    void valorCeroONegativoEsInvalido() {
        Billetera b = nueva("10");
        assertThrows(ValorInvalidoException.class, () -> b.depositar(BigDecimal.ZERO));
        assertThrows(ValorInvalidoException.class, () -> b.depositar(new BigDecimal("-1")));
    }

    @Test
    void noSeCierraConSaldoPositivo() {
        Billetera b = nueva("5");
        assertThrows(OperacionNoPermitidaException.class, b::cerrar);
    }
}
