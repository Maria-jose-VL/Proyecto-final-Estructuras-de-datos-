package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.excepciones.BilleteraInactivaException;
import co.edu.uniquindio.poo.fintech.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.poo.fintech.modelo.Billetera;
import co.edu.uniquindio.poo.fintech.modelo.enums.EstadoTransaccion;
import co.edu.uniquindio.poo.fintech.modelo.enums.NivelRiesgo;
import co.edu.uniquindio.poo.fintech.modelo.enums.ReglaAnomalia;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoBilletera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransaccionServicioTest {

    private PlataformaFintech plataforma;
    private RelojManual reloj;
    private Billetera ahorro;
    private Billetera gastos;

    @BeforeEach
    void preparar() throws Exception {
        reloj = new RelojManual(LocalDateTime.of(2026, 11, 16, 10, 0));
        plataforma = new PlataformaFintech(reloj);
        plataforma.registrarUsuario("1", "Ana", "ana@ejemplo.com");
        plataforma.registrarUsuario("2", "Luis", "luis@ejemplo.com");
        ahorro = plataforma.billeteras().crear("1", "Ahorro", TipoBilletera.AHORRO, new BigDecimal("1000.00"));
        gastos = plataforma.billeteras().crear("2", "Gastos", TipoBilletera.GASTOS_DIARIOS, new BigDecimal("0"));
    }

    @Test
    void transferenciaMueveElDineroYDejaRegistroEnAmbosUsuarios() throws Exception {
        plataforma.transacciones().transferir("1", ahorro.getCodigo(), gastos.getCodigo(), new BigDecimal("200"));
        assertEquals(new BigDecimal("800.00"), ahorro.getSaldo());
        assertEquals(new BigDecimal("200.00"), gastos.getSaldo());
        assertEquals(1, plataforma.usuarios().buscar("1").getHistorial().getSize());
        assertEquals(1, plataforma.usuarios().buscar("2").getHistorial().getSize());
    }

    @Test
    void revertirLaUltimaOperacionRestauraLosSaldos() throws Exception {
        plataforma.transacciones().transferir("1", ahorro.getCodigo(), gastos.getCodigo(), new BigDecimal("200"));
        plataforma.transacciones().revertirUltima("1");
        assertEquals(new BigDecimal("1000.00"), ahorro.getSaldo());
        assertEquals(new BigDecimal("0.00"), gastos.getSaldo());
    }

    @Test
    void noSePuedeRevertirDosVecesLaMismaOperacion() throws Exception {
        plataforma.transacciones().transferir("1", ahorro.getCodigo(), gastos.getCodigo(), new BigDecimal("50"));
        plataforma.transacciones().revertirUltima("1");
        assertThrows(OperacionNoPermitidaException.class, () -> plataforma.transacciones().revertirUltima("1"));
    }

    @Test
    void elLimiteDiarioDeBronceImpideRetirosGrandes() {
        assertThrows(OperacionNoPermitidaException.class, () ->
                plataforma.transacciones().retirar("1", ahorro.getCodigo(), new BigDecimal("1500")));
    }

    @Test
    void unaBilleteraBloqueadaNoAdmiteOperaciones() throws Exception {
        plataforma.billeteras().bloquear(gastos.getCodigo());
        assertThrows(BilleteraInactivaException.class, () ->
                plataforma.transacciones().transferir("1", ahorro.getCodigo(), gastos.getCodigo(), new BigDecimal("10")));
    }

    @Test
    void noSePuedeTransferirALaMismaBilletera() {
        assertThrows(OperacionNoPermitidaException.class, () ->
                plataforma.transacciones().transferir("1", ahorro.getCodigo(), ahorro.getCodigo(), new BigDecimal("10")));
    }

    @Test
    void cuatroOperacionesEnUnMinutoGeneranAlertaDeRafaga() throws Exception {
        for (int i = 0; i < 4; i++) {
            reloj.avanzarHasta(LocalDateTime.of(2026, 11, 16, 10, 0).plusSeconds(10L * i));
            plataforma.transacciones().recargar("1", ahorro.getCodigo(), new BigDecimal("10"));
        }
        boolean hayRafaga = false;
        for (var evento : plataforma.anomalias().eventos()) {
            if (evento.getRegla() == ReglaAnomalia.RAFAGA) {
                hayRafaga = true;
            }
        }
        assertTrue(hayRafaga);
    }

    @Test
    void unMontoMuyGrandeFrenteAlPromedioSeMarcaDeRiesgoAlto() throws Exception {
        plataforma.transacciones().recargar("1", ahorro.getCodigo(), new BigDecimal("10"));
        plataforma.transacciones().recargar("1", ahorro.getCodigo(), new BigDecimal("10"));
        var grande = plataforma.transacciones().recargar("1", ahorro.getCodigo(), new BigDecimal("5000"));
        assertEquals(NivelRiesgo.ALTO, grande.getNivelRiesgo());
        assertEquals(EstadoTransaccion.EXITOSA, grande.getEstado());
    }
}
