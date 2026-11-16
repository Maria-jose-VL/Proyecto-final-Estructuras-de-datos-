package co.edu.uniquindio.poo.fintech.modelo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro de un canje de beneficio. Guarda el nombre y el costo del beneficio
 * tal como estaban al momento del canje, para que el historial no cambie si el catálogo cambia.
 */
public class Canje {

    private final String id;
    private final String usuarioId;
    private final String beneficioId;
    private final String nombreBeneficio;
    private final int puntosGastados;
    private final LocalDateTime fecha;

    public Canje(String id, String usuarioId, String beneficioId, String nombreBeneficio,
                 int puntosGastados, LocalDateTime fecha) {
        this.id = Objects.requireNonNull(id);
        this.usuarioId = Objects.requireNonNull(usuarioId);
        this.beneficioId = Objects.requireNonNull(beneficioId);
        this.nombreBeneficio = Objects.requireNonNull(nombreBeneficio);
        this.puntosGastados = puntosGastados;
        this.fecha = Objects.requireNonNull(fecha);
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getBeneficioId() {
        return beneficioId;
    }

    public String getNombreBeneficio() {
        return nombreBeneficio;
    }

    public int getPuntosGastados() {
        return puntosGastados;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Canje c && id.equals(c.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return nombreBeneficio + " (" + puntosGastados + " pts) el " + fecha.toLocalDate();
    }
}
