package co.edu.uniquindio.poo.fintech.modelo;

import co.edu.uniquindio.poo.fintech.modelo.enums.NivelRiesgo;
import co.edu.uniquindio.poo.fintech.modelo.enums.ReglaAnomalia;

import java.time.LocalDateTime;
import java.util.Objects;

/** Registro inmutable de una transacción marcada como sospechosa. */
public class EventoAuditoria {

    private final String id;
    private final LocalDateTime fecha;
    private final String transaccionId;
    private final String usuarioId;
    private final ReglaAnomalia regla;
    private final NivelRiesgo nivelRiesgo;
    private final String descripcion;

    public EventoAuditoria(String id, LocalDateTime fecha, String transaccionId, String usuarioId,
                           ReglaAnomalia regla, NivelRiesgo nivelRiesgo, String descripcion) {
        this.id = Objects.requireNonNull(id);
        this.fecha = Objects.requireNonNull(fecha);
        this.transaccionId = Objects.requireNonNull(transaccionId);
        this.usuarioId = Objects.requireNonNull(usuarioId);
        this.regla = Objects.requireNonNull(regla);
        this.nivelRiesgo = Objects.requireNonNull(nivelRiesgo);
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getTransaccionId() {
        return transaccionId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public ReglaAnomalia getRegla() {
        return regla;
    }

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return fecha + " [" + nivelRiesgo + "] " + regla + ": " + descripcion;
    }
}
