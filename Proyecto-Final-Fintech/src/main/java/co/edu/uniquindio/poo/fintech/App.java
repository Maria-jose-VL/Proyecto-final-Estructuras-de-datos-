package co.edu.uniquindio.poo.fintech;

import co.edu.uniquindio.poo.fintech.datos.DatosDemo;
import co.edu.uniquindio.poo.fintech.excepciones.FintechException;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;
import co.edu.uniquindio.poo.fintech.ui.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Punto de entrada: crea la plataforma, carga datos de ejemplo y abre la ventana. */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                PlataformaFintech plataforma = new PlataformaFintech();
                DatosDemo.cargar(plataforma);
                new VentanaPrincipal(plataforma).setVisible(true);
            } catch (FintechException e) {
                JOptionPane.showMessageDialog(null, "No se pudieron cargar los datos: " + e.getMessage(),
                        "Error al iniciar", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
