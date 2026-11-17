package co.edu.uniquindio.poo.fintech.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

/** Mensajes emergentes del sistema. */
public final class Dialogos {

    private Dialogos() {
    }

    public static void mostrarError(Component padre, Exception ex) {
        String mensaje = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        JOptionPane.showMessageDialog(padre, mensaje, "Operación no completada", JOptionPane.WARNING_MESSAGE);
    }

    public static void mostrarInfo(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component padre, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, "Confirmar", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }
}