package co.edu.uniquindio.poo.fintech.modelo;

import co.edu.uniquindio.poo.fintech.modelo.enums.EstadoTransaccion;
import co.edu.uniquindio.poo.fintech.modelo.enums.NivelRiesgo;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Un movimiento de dinero. Los datos de origen (id, fecha, tipo, valor, billeteras, usuarios)
 * no cambian. Solo cambian el estado, los puntos, el riesgo y el id de la original en reversiones.
 */
public class Transaccion {

    /** Mayor valor primero; si empatan, por id. */
    public static final Comparator<Transaccion> POR_VALOR =
            Comparator.comparing(Transaccion::getValor).reversed().thenComparing(Transaccion::getId);

    public static final Comparator<Transaccion> POR_FECHA =
            Comparator.comparing(Transaccion::getFecha).thenComparing(Transaccion::getId);

    private final String id;
    private final LocalDateTime fecha;
    private final TipoTransaccion tipo;
    private final BigDecimal valor;
    private final String billeteraOrigen;
    private final String billeteraDestino;
    private final String usuarioOrigenId;
    private final String usuarioDestinoId;
    private final boolean programada;

    private EstadoTransaccion estado;
    private int puntosGenerados;
    private NivelRiesgo nivelRiesgo;
    private String idOriginal;

    public Transaccion(String id, LocalDateTime fecha, TipoTransaccion tipo, BigDecimal valor,
                       String billeteraOrigen, String billeteraDestino,
                       String usuarioOrigenId, String usuarioDestinoId, boolean programada) {
        this.id = Objects.requireNonNull(id);
        this.fecha = Objects.requireNonNull(fecha);
        this.tipo = Objects.requireNonNull(tipo);
        this.valor = Objects.requireNonNull(valor);
        this.billeteraOrigen = billeteraOrigen;
        this.billeteraDestino = billeteraDestino;
        this.usuarioOrigenId = usuarioOrigenId;
        this.usuarioDestinoId = usuarioDestinoId;
        this.programada = programada;
        this.estado = EstadoTransaccion.EXITOSA;
        this.nivelRiesgo = NivelRiesgo.BAJO;
    }

    /** Una transacción puede revertirse si fue exitosa y no es ya una reversión. */
    public boolean esReversible() {
        return estado == EstadoTransaccion.EXITOSA && tipo != TipoTransaccion.REVERSION;
    }

    public void marcarRevertida() {
        estado = EstadoTransaccion.REVERTIDA;
    }

    public void marcarRechazada() {
        estado = EstadoTransaccion.RECHAZADA;
    }

    public boolean involucraBilletera(String codigo) {
        return codigo.equals(billeteraOrigen) || codigo.equals(billeteraDestino);
    }

    public boolean involucraUsuario(String usuarioId) {
        return usuarioId.equals(usuarioOrigenId) || usuarioId.equals(usuarioDestinoId);
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getBilleteraOrigen() {
        return billeteraOrigen;
    }

    public String getBilleteraDestino() {
        return billeteraDestino;
    }

    public String getUsuarioOrigenId() {
        return usuarioOrigenId;
    }

    public String getUsuarioDestinoId() {
        return usuarioDestinoId;
    }

    public boolean isProgramada() {
        return programada;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getIdOriginal() {
        return idOriginal;
    }

    public void setIdOriginal(String idOriginal) {
        this.idOriginal = idOriginal;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Transaccion t && id.equals(t.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " " + tipo + " " + valor + " " + fecha.toLocalDate() + " " + estado;
    }
}
