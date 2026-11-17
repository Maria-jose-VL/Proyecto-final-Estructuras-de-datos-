package co.edu.uniquindio.poo.fintech.ui;

import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelAnomalias;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelBilleteras;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelGrafo;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelHistorial;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelNotificaciones;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelOperaciones;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelProgramadas;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelPuntos;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelRendimiento;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelReportes;
import co.edu.uniquindio.poo.fintech.ui.paneles.PanelUsuarios;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/** Ventana principal: una pestaña por cada área del sistema. */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(PlataformaFintech plataforma) {
        super("Plataforma Fintech - Billeteras digitales");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Usuarios", new PanelUsuarios(plataforma));
        pestanas.addTab("Billeteras", new PanelBilleteras(plataforma));
        pestanas.addTab("Operaciones", new PanelOperaciones(plataforma));
        pestanas.addTab("Programadas", new PanelProgramadas(plataforma));
        pestanas.addTab("Historial", new PanelHistorial(plataforma));
        pestanas.addTab("Puntos", new PanelPuntos(plataforma));
        pestanas.addTab("Anomalías", new PanelAnomalias(plataforma));
        pestanas.addTab("Notificaciones", new PanelNotificaciones(plataforma));
        pestanas.addTab("Reportes", new PanelReportes(plataforma));
        pestanas.addTab("Grafo", new PanelGrafo(plataforma));
        pestanas.addTab("Rendimiento", new PanelRendimiento(plataforma));

        add(pestanas);
        setSize(1100, 720);
        setLocationRelativeTo(null);
    }
}
