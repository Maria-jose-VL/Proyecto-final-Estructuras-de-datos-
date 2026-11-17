package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.excepciones.ValorInvalidoException;
import co.edu.uniquindio.poo.fintech.modelo.Transaccion;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;
import co.edu.uniquindio.poo.fintech.ui.Dialogos;
import co.edu.uniquindio.poo.fintech.ui.Tema;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Base de todos los paneles: una barra de controles arriba y un área de texto para los resultados.
 * Cada botón recibe una acción; si la acción lanza una excepción, se muestra en un diálogo.
 */
public abstract class PanelBase extends JPanel {

    @FunctionalInterface
    protected interface Accion {
        void ejecutar() throws Exception;
    }

    protected final PlataformaFintech plataforma;
    protected final JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    private final JTextArea salida = new JTextArea();

    protected PanelBase(PlataformaFintech plataforma) {
        this.plataforma = plataforma;
        setLayout(new BorderLayout());
        setBackground(Tema.FONDO);
        barra.setBackground(Tema.FONDO);
        salida.setEditable(false);
        salida.setFont(Tema.FUENTE_SALIDA);
        salida.setMargin(new Insets(10, 12, 10, 12));
        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(salida), BorderLayout.CENTER);
    }

    protected JTextField campo(String etiqueta, int columnas) {
        barra.add(new JLabel(etiqueta));
        JTextField campo = new JTextField(columnas);
        barra.add(campo);
        return campo;
    }

    protected <E> JComboBox<E> combo(String etiqueta, E[] opciones) {
        barra.add(new JLabel(etiqueta));
        JComboBox<E> combo = new JComboBox<>(opciones);
        barra.add(combo);
        return combo;
    }

    protected JButton boton(String texto, Accion accion) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.addActionListener(e -> {
            try {
                accion.ejecutar();
            } catch (Exception ex) {
                Dialogos.mostrarError(this, ex);
            }
        });
        barra.add(boton);
        return boton;
    }

    protected void mostrar(String texto) {
        salida.setText(texto);
        salida.setCaretPosition(0);
    }

    /** Texto del campo sin espacios alrededor. */
    protected static String texto(JTextField campo) {
        return campo.getText().trim();
    }

    /** Texto del campo, o null si está vacío. */
    protected static String opcional(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    protected static BigDecimal monto(JTextField campo) throws ValorInvalidoException {
        try {
            return new BigDecimal(campo.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ValorInvalidoException("«" + campo.getText() + "» no es un número");
        }
    }

    protected static int entero(JTextField campo) throws ValorInvalidoException {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValorInvalidoException("«" + campo.getText() + "» no es un número entero");
        }
    }

    protected static LocalDateTime fechaHora(JTextField campo) throws ValorInvalidoException {
        try {
            return LocalDateTime.parse(campo.getText().trim().replace(' ', 'T'));
        } catch (DateTimeParseException e) {
            throw new ValorInvalidoException("la fecha debe tener formato aaaa-mm-dd hh:mm");
        }
    }

    protected static LocalDateTime inicioDelDia(JTextField campo) throws ValorInvalidoException {
        return fecha(campo).atStartOfDay();
    }

    protected static LocalDateTime finDelDia(JTextField campo) throws ValorInvalidoException {
        return fecha(campo).atTime(23, 59, 59);
    }

    private static LocalDate fecha(JTextField campo) throws ValorInvalidoException {
        try {
            return LocalDate.parse(campo.getText().trim());
        } catch (DateTimeParseException e) {
            throw new ValorInvalidoException("la fecha debe tener formato aaaa-mm-dd");
        }
    }

    /** Un elemento por línea, o un aviso si la colección está vacía. */
    protected static String listar(Iterable<?> elementos) {
        StringBuilder sb = new StringBuilder();
        for (Object elemento : elementos) {
            sb.append("- ").append(elemento).append('\n');
        }
        return sb.length() == 0 ? "(sin datos)" : sb.toString();
    }

    protected static String resumen(Transaccion tx) {
        return "Operación " + tx.getId() + " (" + tx.getTipo() + ") por " + tx.getValor() + "\n"
                + "Fecha: " + tx.getFecha() + "\n"
                + "Estado: " + tx.getEstado() + "\n"
                + "Puntos generados: " + tx.getPuntosGenerados() + "\n"
                + "Nivel de riesgo: " + tx.getNivelRiesgo();
    }
}