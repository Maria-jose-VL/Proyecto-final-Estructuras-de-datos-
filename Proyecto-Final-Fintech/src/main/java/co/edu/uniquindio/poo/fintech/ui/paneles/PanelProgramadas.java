package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.modelo.OperacionProgramada;
import co.edu.uniquindio.poo.fintech.modelo.Transaccion;
import co.edu.uniquindio.poo.fintech.modelo.enums.Periodicidad;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class PanelProgramadas extends PanelBase {

    private static final TipoTransaccion[] TIPOS_PROGRAMABLES = {
            TipoTransaccion.RECARGA, TipoTransaccion.RETIRO, TipoTransaccion.TRANSFERENCIA
    };

    public PanelProgramadas(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField usuario = campo("Usuario", 7);
        JComboBox<TipoTransaccion> tipo = combo("Tipo", TIPOS_PROGRAMABLES);
        JTextField origen = campo("Origen", 8);
        JTextField destino = campo("Destino", 8);
        JTextField importe = campo("Valor", 6);
        JTextField fecha = campo("Fecha (aaaa-mm-dd hh:mm)", 13);
        JComboBox<Periodicidad> periodicidad = combo("Periodicidad", Periodicidad.values());
        JTextField codigo = campo("Código", 8);

        boton("Programar", () -> {
            OperacionProgramada operacion = plataforma.programadas().programar(
                    texto(usuario), (TipoTransaccion) tipo.getSelectedItem(),
                    opcional(origen), opcional(destino), monto(importe), fechaHora(fecha),
                    (Periodicidad) periodicidad.getSelectedItem());
            mostrar("Operación programada:\n" + operacion);
        });
        boton("Ejecutar vencidas", () -> {
            ListaEnlazada<Transaccion> ejecutadas = plataforma.programadas().ejecutarVencidas();
            mostrar(ejecutadas.getSize() + " operaciones ejecutadas:\n" + listar(ejecutadas));
        });
        boton("Cancelar", () -> {
            plataforma.programadas().cancelar(texto(codigo));
            mostrar("Programación " + texto(codigo) + " cancelada.");
        });
        boton("Ver de usuario", () -> mostrar(listar(plataforma.programadas().listarDe(texto(usuario)))));
    }
}
