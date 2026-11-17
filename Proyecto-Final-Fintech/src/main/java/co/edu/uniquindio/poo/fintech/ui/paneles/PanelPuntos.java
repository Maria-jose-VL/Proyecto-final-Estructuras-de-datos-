package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.modelo.Canje;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JTextField;

public class PanelPuntos extends PanelBase {

    public PanelPuntos(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField usuario = campo("Usuario", 8);
        JTextField cantidad = campo("N", 4);
        JTextField beneficio = campo("Beneficio", 5);

        boton("Puntos y nivel", () -> {
            Usuario u = plataforma.usuarios().buscar(texto(usuario));
            mostrar("Usuario: " + u.getNombre() + "\n"
                    + "Puntos: " + u.getPuntos() + "\n"
                    + "Nivel: " + u.getNivel() + "\n"
                    + "Límite diario: " + u.getNivel().getLimiteDiario());
        });
        boton("Top N", () -> mostrar(listar(plataforma.reportes().topPuntos(entero(cantidad)))));
        boton("Ranking completo", () -> mostrar(listar(plataforma.recompensas().ranking())));
        boton("Beneficios disponibles", () -> mostrar(listar(plataforma.recompensas().listarBeneficios(
                plataforma.usuarios().buscar(texto(usuario)).getNivel()))));
        boton("Canjear", () -> {
            Canje canje = plataforma.recompensas().canjear(texto(usuario), texto(beneficio));
            mostrar("Canje realizado:\n" + canje + "\nPuntos restantes: "
                    + plataforma.usuarios().buscar(texto(usuario)).getPuntos());
        });
    }
}
