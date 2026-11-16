package co.edu.uniquindio.poo.fintech.modelo;

import co.edu.uniquindio.poo.fintech.modelo.enums.EstadoProgramada;
import co.edu.uniquindio.poo.fintech.modelo.enums.Periodicidad;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/** Movimiento que se ejecutará en una fecha futura, una o varias veces. */
public class OperacionProgramada {

    /** La más próxima primero; si empatan, por id. */
    public static final Comparator<OperacionProgramada> POR_FECHA =
            Comparator.comparing(OperacionProgramada::getFechaEjecucion).thenComparing(OperacionProgramada::getId);

    private final String id;
    private final String usuarioId;
    private final TipoTransaccion tipo;
    private final String billeteraOrigen;
    private final String billeteraDestino;
    private final BigDecimal valor;
    private final LocalDateTime fechaEjecucion;
    private final Periodicidad periodicidad;
    private EstadoProgramada estado;

    public OperacionProgramada(String id, String usuarioId, TipoTransaccion tipo,
                               String billeteraOrigen, String billeteraDestino, BigDecimal valor,
                               LocalDateTime fechaEjecucion, Periodicidad periodicidad) {
        this.id = Objects.requireNonNull(id);
        this.usuarioId = Objects.requireNonNull(usuarioId);
        this.tipo = Objects.requireNonNull(tipo);
        this.billeteraOrigen = billeteraOrigen;
        this.billeteraDestino = billeteraDestino;
        this.valor = Objects.requireNonNull(valor);
        this.fechaEjecucion = Objects.requireNonNull(fechaEjecucion);
        this.periodicidad = Objects.requireNonNull(periodicidad);
        this.estado = EstadoProgramada.PENDIENTE;
    }

    public boolean estaVencida(LocalDateTime ahora) {
        return estado == EstadoProgramada.PENDIENTE && !fechaEjecucion.isAfter(ahora);
    }

    public void marcarEjecutada() {
        estado = EstadoProgramada.EJECUTADA;
    }

    public void marcarFallida() {
        estado = EstadoProgramada.FALLIDA;
    }

    public void cancelar() {
        estado = EstadoProgramada.CANCELADA;
    }

    /**
     * Crea la siguiente ejecución de una operación periódica con un id nuevo.
     * Devuelve null si la operación es única.
     */
    public OperacionProgramada crearSiguiente(String nuevoId) {
        LocalDateTime siguiente = periodicidad.siguienteFecha(fechaEjecucion);
        if (siguiente == null) {
            return null;
        }
        return new OperacionProgramada(nuevoId, usuarioId, tipo, billeteraOrigen,
                billeteraDestino, valor, siguiente, periodicidad);
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public String getBilleteraOrigen() {
        return billeteraOrigen;
    }

    public String getBilleteraDestino() {
        return billeteraDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public Periodicidad getPeriodicidad() {
        return periodicidad;
    }

    public EstadoProgramada getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof OperacionProgramada p && id.equals(p.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " " + tipo + " " + valor + " " + fechaEjecucion + " " + periodicidad + " " + estado;
    }
}
