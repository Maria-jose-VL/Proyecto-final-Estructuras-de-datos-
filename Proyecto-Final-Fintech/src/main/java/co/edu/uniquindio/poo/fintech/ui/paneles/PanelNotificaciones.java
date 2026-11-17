package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelNotificaciones extends PanelBase {

    public PanelNotificaciones(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField usuario = campo("Usuario", 8);

        boton("Recientes", () ->
                mostrar(listar(plataforma.notificaciones().consultarRecientes(texto(usuario)))));
        boton("Marcar leídas", () -> mostrar(plataforma.notificaciones().marcarLeidas(texto(usuario))
                + " notificaciones marcadas como leídas."));
        boton("Despachar pendientes", () -> mostrar(plataforma.notificaciones().despacharTodas()
                + " notificaciones enviadas desde la cola de pendientes."));
    }
}