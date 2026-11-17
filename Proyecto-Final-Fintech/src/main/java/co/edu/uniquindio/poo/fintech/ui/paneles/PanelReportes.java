package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.estructuras.hash.TablaHash;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelReportes extends PanelBase {

    public PanelReportes(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField n = campo("N", 4);
        JTextField desde = campo("Desde (aaaa-mm-dd)", 10);
        JTextField hasta = campo("Hasta (aaaa-mm-dd)", 10);

        boton("Conteo por tipo", () -> {
            TablaHash<TipoTransaccion, Integer> conteo = plataforma.reportes().conteoPorTipo();
            StringBuilder sb = new StringBuilder();
            for (TipoTransaccion tipo : TipoTransaccion.values()) {
                sb.append(tipo).append(": ").append(conteo.get(tipo).orElse(0)).append('\n');
            }
            mostrar(sb.toString());
        });
        boton("Mayores transacciones", () -> mostrar(listar(plataforma.reportes().mayoresTransacciones(entero(n)))));
        boton("Relaciones frecuentes", () -> mostrar(listar(plataforma.reportes().relacionesMasFrecuentes(entero(n)))));
        boton("Ciclos entre usuarios", () -> {
            ListaEnlazada<ListaEnlazada<String>> ciclos = plataforma.reportes().ciclosDeRelaciones();
            mostrar(ciclos.getSize() + " ciclos encontrados:\n" + listar(ciclos));
        });
        boton("Operaciones del período", () -> mostrar(listar(
                plataforma.reportes().operacionesEntre(inicioDelDia(desde), finDelDia(hasta)))));
        boton("Canjes del período", () -> mostrar(listar(
                plataforma.reportes().canjesEntre(inicioDelDia(desde), finDelDia(hasta)))));
    }
}
