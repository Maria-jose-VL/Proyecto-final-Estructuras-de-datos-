package co.edu.uniquindio.poo.fintech.ui.paneles;

import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.servicio.PlataformaFintech;
import co.edu.uniquindio.poo.fintech.ui.Dialogos;

import javax.swing.JTextField;

public class PanelUsuarios extends PanelBase {

    public PanelUsuarios(PlataformaFintech plataforma) {
        super(plataforma);
        JTextField documento = campo("Documento", 10);
        JTextField nombre = campo("Nombre", 16);
        JTextField correo = campo("Correo", 18);

        boton("Registrar", () -> {
            Usuario usuario = plataforma.registrarUsuario(texto(documento), texto(nombre), texto(correo));
            mostrar("Usuario registrado:\n" + usuario);
        });
        boton("Ver todos", () -> mostrar(listar(plataforma.usuarios().listar())));
        boton("Eliminar", () -> {
            String id = texto(documento);
            if (Dialogos.confirmar(this, "¿Eliminar el usuario " + id + " y sus billeteras?")) {
                plataforma.eliminarUsuario(id);
                mostrar("Usuario " + id + " eliminado.");
            }
        });
    }
}