package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.rendimiento.ComparadorEstructuras;
import co.edu.uniquindio.poo.fintech.rendimiento.ResultadoMedicion;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelRendimiento extends PanelBase {

    public PanelRendimiento(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField n = campo("Cantidad de datos (n)", 7);
        n.setText("2000");

        boton("Medir", () -> {
            ListaEnlazada<ResultadoMedicion> resultados = new ComparadorEstructuras().comparar(entero(n));
            StringBuilder sb = new StringBuilder(
                    String.format("%-14s %-10s %14s%n", "Estructura", "Operación", "Milisegundos"));
            for (ResultadoMedicion m : resultados) {
                sb.append(String.format("%-14s %-10s %14.3f%n", m.estructura(), m.operacion(), m.milisegundos()));
            }
            mostrar(sb.toString());
        });
    }
}
