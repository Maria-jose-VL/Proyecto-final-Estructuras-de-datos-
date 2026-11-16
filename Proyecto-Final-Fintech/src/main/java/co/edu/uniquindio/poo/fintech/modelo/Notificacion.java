package co.edu.uniquindio.poo.fintech.modelo;

import co.edu.uniquindio.poo.fintech.modelo.enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.Objects;

/** Alerta enviada a un usuario. */
public class Notificacion {

    private final String id;
    private final String usuarioId;
    private final TipoNotificacion tipo;
    private final String mensaje;
    private final LocalDateTime fecha;
    private boolean leida;

    public Notificacion(String id, String usuarioId, TipoNotificacion tipo, String mensaje, LocalDateTime fecha) {
        this.id = Objects.requireNonNull(id);
        this.usuarioId = Objects.requireNonNull(usuarioId);
        this.tipo = Objects.requireNonNull(tipo);
        this.mensaje = Objects.requireNonNull(mensaje);
        this.fecha = Objects.requireNonNull(fecha);
    }

    public void marcarLeida() {
        leida = true;
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isLeida() {
        return leida;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + mensaje;
    }
}