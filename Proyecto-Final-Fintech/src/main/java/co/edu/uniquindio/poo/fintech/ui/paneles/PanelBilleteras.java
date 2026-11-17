package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.modelo.Billetera;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoBilletera;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class PanelBilleteras extends PanelBase {

    public PanelBilleteras(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField usuario = campo("Usuario", 8);
        JTextField nombre = campo("Nombre", 10);
        JComboBox<TipoBilletera> tipo = combo("Tipo", TipoBilletera.values());
        JTextField saldoInicial = campo("Saldo inicial", 8);
        JTextField codigo = campo("Código", 9);

        boton("Crear billetera", () -> {
            Billetera billetera = plataforma.billeteras().crear(texto(usuario), texto(nombre),
                    (TipoBilletera) tipo.getSelectedItem(), monto(saldoInicial));
            mostrar("Billetera creada:\n" + billetera);
        });
        boton("Billeteras del usuario", () ->
                mostrar(listar(plataforma.usuarios().buscar(texto(usuario)).getBilleteras())));
        boton("Ver saldo", () -> {
            Billetera billetera = plataforma.billeteras().buscar(texto(codigo));
            mostrar(billetera + "\nSaldo: " + billetera.getSaldo());
        });
        boton("Bloquear", () -> {
            plataforma.billeteras().bloquear(texto(codigo));
            mostrar("Billetera " + texto(codigo) + " bloqueada.");
        });
        boton("Activar", () -> {
            plataforma.billeteras().activar(texto(codigo));
            mostrar("Billetera " + texto(codigo) + " activa de nuevo.");
        });
        boton("Cerrar", () -> {
            plataforma.billeteras().cerrar(texto(codigo));
            mostrar("Billetera " + texto(codigo) + " cerrada.");
        });
    }
}
