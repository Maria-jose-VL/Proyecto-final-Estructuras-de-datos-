package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelHistorial extends PanelBase {

    public PanelHistorial(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField usuario = campo("Usuario", 8);
        JTextField codigo = campo("Billetera", 9);

        boton("Historial del usuario", () ->
                mostrar(listar(plataforma.usuarios().buscar(texto(usuario)).getHistorial())));
        boton("Movimientos de la billetera", () ->
                mostrar(listar(plataforma.billeteras().buscar(texto(codigo)).getHistorial())));
    }
}
