package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.colas.Cola;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.poo.fintech.modelo.Beneficio;
import co.edu.uniquindio.poo.fintech.modelo.Billetera;
import co.edu.uniquindio.poo.fintech.modelo.Notificacion;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoNotificacion;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Las alertas se crean y quedan en una cola de pendientes; al despacharlas pasan
 * a la cola de notificaciones recientes del usuario (máximo 20).
 */
public class NotificacionServicio {

    private final Cola<Notificacion> pendientes = new Cola<>();
    private final UsuarioServicio usuarioServicio;
    private final Clock reloj;
    private int contador = 1;

    public NotificacionServicio(UsuarioServicio usuarioServicio, Clock reloj) {
        this.usuarioServicio = usuarioServicio;
        this.reloj = reloj;
    }

    public Notificacion notificar(Usuario usuario, TipoNotificacion tipo, String mensaje) {
        Notificacion n = new Notificacion(String.format("N%04d", contador++), usuario.getId(), tipo,
                mensaje, LocalDateTime.now(reloj));
        pendientes.encolar(n);
        return n;
    }

    /** Saca la siguiente pendiente y la entrega al usuario. Devuelve null si no hay. */
    public Notificacion despacharSiguiente() {
        if (pendientes.isEmpty()) {
            return null;
        }
        Notificacion n = pendientes.desencolar();
        try {
            usuarioServicio.buscar(n.getUsuarioId()).agregarNotificacion(n);
        } catch (UsuarioNoEncontradoException e) {
            // el usuario fue eliminado: la alerta se descarta
        }
        return n;
    }

    public int despacharTodas() {
        int cantidad = 0;
        while (despacharSiguiente() != null) {
            cantidad++;
        }
        return cantidad;
    }

    public int cantidadPendientes() {
        return pendientes.getSize();
    }

    public ListaEnlazada<Notificacion> consultarRecientes(String usuarioId) throws UsuarioNoEncontradoException {
        ListaEnlazada<Notificacion> lista = new ListaEnlazada<>();
        for (Notificacion n : usuarioServicio.buscar(usuarioId).getNotificaciones()) {
            lista.addLast(n);
        }
        return lista;
    }

    public void marcarLeidas(String usuarioId) throws UsuarioNoEncontradoException {
        for (Notificacion n : usuarioServicio.buscar(usuarioId).getNotificaciones()) {
            n.marcarLeida();
        }
    }

    public void alertarSaldoBajo(Usuario usuario, Billetera billetera) {
        notificar(usuario, TipoNotificacion.SALDO_BAJO, "La billetera " + billetera.getCodigo()
                + " (" + billetera.getNombre() + ") quedó con saldo bajo: $" + billetera.getSaldo().toPlainString());
    }

    public void alertarRechazo(Usuario usuario, String motivo) {
        notificar(usuario, TipoNotificacion.OPERACION_RECHAZADA, "Operación rechazada: " + motivo);
    }

    public void alertarAscenso(Usuario usuario) {
        notificar(usuario, TipoNotificacion.ASCENSO_NIVEL, "¡Subiste al nivel " + usuario.getNivel() + "!");
    }

    public void alertarCanje(Usuario usuario, Beneficio beneficio) {
        notificar(usuario, TipoNotificacion.CANJE_BENEFICIO, "Canjeaste el beneficio: " + beneficio.getNombre());
    }

    public void alertarSeguridad(Usuario usuario, String descripcion) {
        notificar(usuario, TipoNotificacion.ALERTA_SEGURIDAD, descripcion);
    }
}
