package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelAnomalias extends PanelBase {

    public PanelAnomalias(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField usuario = campo("Usuario", 8);

        boton("Todos los eventos", () -> mostrar(listar(plataforma.anomalias().eventos())));
        boton("Eventos del usuario", () -> mostrar(listar(plataforma.anomalias().eventosDe(texto(usuario)))));
    }
}
