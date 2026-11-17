package co.edu.uniquindio.poo.fintech.datos;

import co.edu.uniquindio.poo.fintech.excepciones.FintechException;
import co.edu.uniquindio.poo.fintech.modelo.Beneficio;
import co.edu.uniquindio.poo.fintech.modelo.Billetera;
import co.edu.uniquindio.poo.fintech.modelo.enums.NivelUsuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoBilletera;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import java.math.BigDecimal;

/** Carga usuarios, billeteras, beneficios y operaciones de ejemplo para la demostración. */
public final class DatosDemo {

    private DatosDemo() {
    }

    public static void cargar(PlataformaFintech plataforma) throws FintechException {
        plataforma.registrarUsuario("1001", "Ana Rojas", "ana@ejemplo.com");
        plataforma.registrarUsuario("1002", "Luis Mena", "luis@ejemplo.com");
        plataforma.registrarUsuario("1003", "Sara Gil", "sara@ejemplo.com");

        plataforma.recompensas().registrarBeneficio(
                new Beneficio("B01", "Descuento del 10%", "Descuento en comisiones", 150, NivelUsuario.BRONCE));
        plataforma.recompensas().registrarBeneficio(
                new Beneficio("B02", "Envío gratis", "Sin costo en retiros", 300, NivelUsuario.PLATA));
        plataforma.recompensas().registrarBeneficio(
                new Beneficio("B03", "Cashback del 5%", "Devolución sobre recargas", 800, NivelUsuario.ORO));
        plataforma.recompensas().registrarBeneficio(
                new Beneficio("B04", "Asesoría premium", "Sesión con un asesor", 3000, NivelUsuario.PLATINO));

        var billeteras = plataforma.billeteras();
        Billetera anaAhorro = billeteras.crear("1001", "Ahorro", TipoBilletera.AHORRO, new BigDecimal("500.00"));
        Billetera anaGastos = billeteras.crear("1001", "Gastos", TipoBilletera.GASTOS_DIARIOS, new BigDecimal("120.00"));
        Billetera luisGastos = billeteras.crear("1002", "Gastos", TipoBilletera.GASTOS_DIARIOS, new BigDecimal("80.00"));
        Billetera saraTransporte = billeteras.crear("1003", "Transporte", TipoBilletera.TRANSPORTE, new BigDecimal("250.00"));

        var transacciones = plataforma.transacciones();
        transacciones.recargar("1001", anaGastos.getCodigo(), new BigDecimal("300.00"));
        transacciones.transferir("1001", anaAhorro.getCodigo(), anaGastos.getCodigo(), new BigDecimal("100.00"));
        transacciones.transferir("1001", anaGastos.getCodigo(), luisGastos.getCodigo(), new BigDecimal("150.00"));
        transacciones.transferir("1002", luisGastos.getCodigo(), saraTransporte.getCodigo(), new BigDecimal("40.00"));
        transacciones.transferir("1003", saraTransporte.getCodigo(), luisGastos.getCodigo(), new BigDecimal("60.00"));
        transacciones.retirar("1002", luisGastos.getCodigo(), new BigDecimal("25.00"));

        plataforma.recompensas().otorgarPuntos("1001", 600);
    }
}
