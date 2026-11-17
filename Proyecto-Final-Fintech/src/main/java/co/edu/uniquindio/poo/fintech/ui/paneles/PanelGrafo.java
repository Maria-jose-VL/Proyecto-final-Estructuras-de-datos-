package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelGrafo extends PanelBase {

    public PanelGrafo(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField origen = campo("Usuario origen", 8);
        JTextField destino = campo("Usuario destino", 8);

        boton("Vecinos", () -> mostrar(listar(
                plataforma.transacciones().relaciones().vecinos(texto(origen)))));
        boton("Recorrido en anchura (BFS)", () -> mostrar(listar(
                plataforma.transacciones().relaciones().recorridoBFS(texto(origen)))));
        boton("Recorrido en profundidad (DFS)", () -> mostrar(listar(
                plataforma.transacciones().relaciones().recorridoDFS(texto(origen)))));
        boton("¿Hay camino?", () -> {
            boolean hay = plataforma.transacciones().relaciones().existeCamino(texto(origen), texto(destino));
            mostrar(hay ? "Sí hay camino de " + texto(origen) + " a " + texto(destino)
                    : "No hay camino de " + texto(origen) + " a " + texto(destino));
        });
    }
}
