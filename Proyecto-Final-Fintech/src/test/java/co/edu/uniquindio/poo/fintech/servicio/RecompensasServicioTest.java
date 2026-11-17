package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.excepciones.PuntosInsuficientesException;
import co.edu.uniquindio.poo.fintech.modelo.Beneficio;
import co.edu.uniquindio.poo.fintech.modelo.enums.NivelUsuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecompensasServicioTest {

    private PlataformaFintech plataforma;

    @BeforeEach
    void preparar() throws Exception {
        plataforma = new PlataformaFintech(new RelojManual(LocalDateTime.of(2026, 11, 16, 10, 0)));
        plataforma.registrarUsuario("1", "Ana", "ana@ejemplo.com");
        plataforma.registrarUsuario("2", "Luis", "luis@ejemplo.com");
    }

    @Test
    void calculoDePuntosPorTipoYBonoProgramada() {
        RecompensasServicio r = plataforma.recompensas();
        assertEquals(3, r.calcularPuntos(TipoTransaccion.RECARGA, new BigDecimal("300"), false));
        assertEquals(6, r.calcularPuntos(TipoTransaccion.TRANSFERENCIA, new BigDecimal("250"), false));
        assertEquals(2 + 10, r.calcularPuntos(TipoTransaccion.RETIRO, new BigDecimal("100"), true));
    }

    @Test
    void losNivelesSeCalculanPorPuntos() throws Exception {
        plataforma.recompensas().otorgarPuntos("1", 501);
        assertEquals(NivelUsuario.PLATA, plataforma.usuarios().buscar("1").getNivel());
        assertEquals(NivelUsuario.BRONCE, plataforma.usuarios().buscar("2").getNivel());
    }

    @Test
    void elRankingVaDePuntosMayoresAMenores() throws Exception {
        plataforma.recompensas().otorgarPuntos("2", 50);
        plataforma.recompensas().otorgarPuntos("1", 900);
        assertEquals("1", plataforma.recompensas().topUsuarios(1).getFirst().getId());
    }

    @Test
    void canjearDescuentaPuntosYRegistraElCanje() throws Exception {
        plataforma.recompensas().otorgarPuntos("1", 600);
        plataforma.recompensas().registrarBeneficio(
                new Beneficio("B01", "Descuento", "Comisiones", 150, NivelUsuario.BRONCE));
        var canje = plataforma.recompensas().canjear("1", "B01");
        assertEquals(150, canje.getPuntosGastados());
        assertEquals(450, plataforma.usuarios().buscar("1").getPuntos());
        assertEquals(1, plataforma.usuarios().buscar("1").getCanjes().getSize());
    }

    @Test
    void noSePuedeCanjearUnBeneficioDeUnNivelSuperior() throws Exception {
        plataforma.recompensas().otorgarPuntos("1", 600);
        plataforma.recompensas().registrarBeneficio(
                new Beneficio("B09", "Premium", "Asesoría", 100, NivelUsuario.PLATINO));
        assertThrows(PuntosInsuficientesException.class, () ->
                plataforma.recompensas().canjear("1", "B09"));
    }
}