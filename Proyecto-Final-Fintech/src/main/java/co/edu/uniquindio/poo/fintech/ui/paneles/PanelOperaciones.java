package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelOperaciones extends PanelBase {

    public PanelOperaciones(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField usuario = campo("Usuario", 8);
        JTextField origen = campo("Billetera origen", 9);
        JTextField destino = campo("Billetera destino", 9);
        JTextField importe = campo("Valor", 8);

        boton("Recargar", () -> mostrar(resumen(
                plataforma.transacciones().recargar(texto(usuario), texto(destino), monto(importe)))));
        boton("Retirar", () -> mostrar(resumen(
                plataforma.transacciones().retirar(texto(usuario), texto(origen), monto(importe)))));
        boton("Transferir", () -> mostrar(resumen(
                plataforma.transacciones().transferir(texto(usuario), texto(origen), texto(destino), monto(importe)))));
        boton("Deshacer última", () -> mostrar("Reversión creada:\n"
                + resumen(plataforma.transacciones().revertirUltima(texto(usuario)))));
    }
}
