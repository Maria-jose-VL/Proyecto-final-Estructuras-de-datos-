package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.arboles.ArbolAVL;
import co.edu.uniquindio.poo.fintech.estructuras.grafos.Grafo;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.BilleteraInactivaException;
import co.edu.uniquindio.poo.fintech.excepciones.FintechException;
import co.edu.uniquindio.poo.fintech.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.poo.fintech.excepciones.ValorInvalidoException;
import co.edu.uniquindio.poo.fintech.modelo.Billetera;
import co.edu.uniquindio.poo.fintech.modelo.Transaccion;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.EstadoTransaccion;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ejecuta recargas, retiros, transferencias y reversiones.
 * Guarda el registro global de transacciones, un AVL ordenado por valor
 * y el grafo de transferencias entre usuarios.
 */
public class TransaccionServicio {

    private static final BigDecimal UMBRAL_SALDO_BAJO = new BigDecimal("50.00");

    private final ListaEnlazada<Transaccion> todas = new ListaEnlazada<>();
    private final ArbolAVL<Transaccion> porValor = new ArbolAVL<>(Transaccion.POR_VALOR);
    private final Grafo<String> relaciones = new Grafo<>();
    private final UsuarioServicio usuarios;
    private final BilleteraServicio billeteras;
    private final RecompensasServicio recompensas;
    private final AnomaliaServicio anomalias;
    private final NotificacionServicio notificaciones;
    private final Clock clock;
    private int contador;

    public TransaccionServicio(UsuarioServicio usuarios, BilleteraServicio billeteras,
                               RecompensasServicio recompensas, AnomaliaServicio anomalias,
                               NotificacionServicio notificaciones, Clock clock) {
        this.usuarios = usuarios;
        this.billeteras = billeteras;
        this.recompensas = recompensas;
        this.anomalias = anomalias;
        this.notificaciones = notificaciones;
        this.clock = clock;
    }

    public void registrarVertice(String usuarioId) {
        relaciones.agregarVertice(usuarioId);
    }

    public Transaccion recargar(String usuarioId, String billeteraDestino, BigDecimal valor) throws FintechException {
        return ejecutar(TipoTransaccion.RECARGA, usuarioId, null, billeteraDestino, valor, false);
    }

    public Transaccion retirar(String usuarioId, String billeteraOrigen, BigDecimal valor) throws FintechException {
        return ejecutar(TipoTransaccion.RETIRO, usuarioId, billeteraOrigen, null, valor, false);
    }

    public Transaccion transferir(String usuarioId, String billeteraOrigen, String billeteraDestino,
                                  BigDecimal valor) throws FintechException {
        return ejecutar(TipoTransaccion.TRANSFERENCIA, usuarioId, billeteraOrigen, billeteraDestino, valor, false);
    }

    /** Punto de entrada común. Las programadas lo usan con programada = true. */
    public Transaccion ejecutar(TipoTransaccion tipo, String usuarioId, String billeteraOrigen,
                                String billeteraDestino, BigDecimal valor, boolean programada)
            throws FintechException {
        if (valor == null || valor.signum() <= 0) {
            throw new ValorInvalidoException("el valor debe ser mayor que cero");
        }
        BigDecimal monto = valor.setScale(2, RoundingMode.HALF_UP);
        Usuario actor = usuarios.buscar(usuarioId);
        Billetera origen = billeteraOrigen == null ? null : billeteras.buscar(billeteraOrigen);
        Billetera destino = billeteraDestino == null ? null : billeteras.buscar(billeteraDestino);
        validarOperacion(tipo, usuarioId, origen, destino);
        if (tipo == TipoTransaccion.RETIRO || tipo == TipoTransaccion.TRANSFERENCIA) {
            verificarLimiteDiario(actor, monto);
        }

        ejecutarMovimiento(tipo, origen, destino, monto);

        Transaccion tx = new Transaccion(siguienteId(), LocalDateTime.now(clock), tipo, monto,
                billeteraOrigen, billeteraDestino,
                origen == null ? null : origen.getUsuarioId(),
                destino == null ? null : destino.getUsuarioId(),
                programada);
        tx.setPuntosGenerados(recompensas.calcularPuntos(tipo, monto, programada));
        registrar(tx);

        if (tipo == TipoTransaccion.TRANSFERENCIA) {
            relaciones.agregarVertice(tx.getUsuarioOrigenId());
            relaciones.agregarVertice(tx.getUsuarioDestinoId());
            relaciones.agregarArista(tx.getUsuarioOrigenId(), tx.getUsuarioDestinoId());
        }
        actor.apilarParaDeshacer(tx);
        if (tx.getPuntosGenerados() > 0) {
            recompensas.otorgarPuntos(usuarioId, tx.getPuntosGenerados());
        }
        anomalias.evaluar(tx, actor);
        if (origen != null && origen.getSaldo().compareTo(UMBRAL_SALDO_BAJO) < 0) {
            notificaciones.alertarSaldoBajo(usuarioId, origen.getCodigo(), origen.getSaldo());
        }
        return tx;
    }

    /** Deshace la última operación del usuario, si todavía se puede revertir. */
    public Transaccion revertirUltima(String usuarioId) throws FintechException {
        Usuario actor = usuarios.buscar(usuarioId);
        if (!actor.tieneReversibles()) {
            throw new OperacionNoPermitidaException("no hay operaciones para deshacer");
        }
        Transaccion original = actor.desapilarUltima();
        if (!original.esReversible()) {
            throw new OperacionNoPermitidaException("la operación " + original.getId() + " ya no se puede revertir");
        }
        Billetera origen = original.getBilleteraOrigen() == null
                ? null : billeteras.buscar(original.getBilleteraOrigen());
        Billetera destino = original.getBilleteraDestino() == null
                ? null : billeteras.buscar(original.getBilleteraDestino());
        try {
            revertirMovimiento(original.getTipo(), origen, destino, original.getValor());
        } catch (FintechException e) {
            actor.apilarParaDeshacer(original);
            throw e;
        }
        original.marcarRevertida();
        recompensas.descontarPuntos(usuarioId, original.getPuntosGenerados());

        Transaccion reversion = new Transaccion(siguienteId(), LocalDateTime.now(clock),
                TipoTransaccion.REVERSION, original.getValor(),
                original.getBilleteraOrigen(), original.getBilleteraDestino(),
                original.getUsuarioOrigenId(), original.getUsuarioDestinoId(), false);
        reversion.setIdOriginal(original.getId());
        registrar(reversion);
        notificaciones.alertarReversion(usuarioId, "Revertiste la operación " + original.getId());
        return reversion;
    }

    public ListaEnlazada<Transaccion> todas() {
        return todas.filtrar(t -> true);
    }

    /** Las n transacciones de mayor valor, de mayor a menor. */
    public ListaEnlazada<Transaccion> mayores(int n) {
        return porValor.obtenerMayores(n);
    }

    public Grafo<String> relaciones() {
        return relaciones;
    }

    public int getTotal() {
        return todas.getSize();
    }

    private void validarOperacion(TipoTransaccion tipo, String usuarioId, Billetera origen, Billetera destino)
            throws FintechException {
        if (origen != null) {
            if (!origen.getUsuarioId().equals(usuarioId)) {
                throw new OperacionNoPermitidaException("la billetera " + origen.getCodigo() + " no es tuya");
            }
            verificarActiva(origen);
        }
        if (destino != null) {
            verificarActiva(destino);
        }
        switch (tipo) {
            case RECARGA -> {
                if (destino == null || !destino.getUsuarioId().equals(usuarioId)) {
                    throw new OperacionNoPermitidaException("solo puedes recargar tus propias billeteras");
                }
            }
            case RETIRO -> {
                if (origen == null) {
                    throw new ValorInvalidoException("un retiro necesita una billetera de origen");
                }
            }
            case TRANSFERENCIA -> {
                if (origen == null || destino == null) {
                    throw new ValorInvalidoException("una transferencia necesita billetera de origen y de destino");
                }
                if (origen.equals(destino)) {
                    throw new OperacionNoPermitidaException("no puedes transferir a la misma billetera");
                }
            }
            default -> throw new OperacionNoPermitidaException("el tipo " + tipo + " no se ejecuta directamente");
        }
    }

    private static void verificarActiva(Billetera billetera) throws BilleteraInactivaException {
        if (!billetera.estaActiva()) {
            throw new BilleteraInactivaException(billetera.getCodigo(), billetera.getEstado().name());
        }
    }

    /** Suma lo que el usuario ya retiró o transfirió hoy y compara con su límite de nivel. */
    private void verificarLimiteDiario(Usuario actor, BigDecimal monto) throws OperacionNoPermitidaException {
        LocalDate hoy = LocalDate.now(clock);
        BigDecimal acumulado = BigDecimal.ZERO;
        for (Transaccion t : actor.getHistorial()) {
            boolean esSalida = t.getTipo() == TipoTransaccion.RETIRO || t.getTipo() == TipoTransaccion.TRANSFERENCIA;
            if (esSalida && actor.getId().equals(t.getUsuarioOrigenId())
                    && t.getEstado() == EstadoTransaccion.EXITOSA
                    && t.getFecha().toLocalDate().equals(hoy)) {
                acumulado = acumulado.add(t.getValor());
            }
        }
        BigDecimal limite = actor.getNivel().getLimiteDiario();
        if (acumulado.add(monto).compareTo(limite) > 0) {
            throw new OperacionNoPermitidaException("superarías tu límite diario de " + limite
                    + " (hoy llevas " + acumulado + ")");
        }
    }

    private static void ejecutarMovimiento(TipoTransaccion tipo, Billetera origen, Billetera destino,
                                           BigDecimal monto) throws FintechException {
        switch (tipo) {
            case RECARGA -> destino.depositar(monto);
            case RETIRO -> origen.retirar(monto);
            case TRANSFERENCIA -> {
                origen.retirar(monto);
                try {
                    destino.depositar(monto);
                } catch (FintechException e) {
                    origen.depositar(monto);
                    throw e;
                }
            }
            default -> throw new OperacionNoPermitidaException("el tipo " + tipo + " no se ejecuta directamente");
        }
    }

    private static void revertirMovimiento(TipoTransaccion tipo, Billetera origen, Billetera destino,
                                           BigDecimal monto) throws FintechException {
        switch (tipo) {
            case RECARGA -> destino.retirar(monto);
            case RETIRO -> origen.depositar(monto);
            case TRANSFERENCIA -> {
                destino.retirar(monto);
                origen.depositar(monto);
            }
            default -> throw new OperacionNoPermitidaException("el tipo " + tipo + " no se puede revertir");
        }
    }

    private void registrar(Transaccion tx) {
        todas.addLast(tx);
        porValor.insertar(tx);
        if (tx.getBilleteraOrigen() != null) {
            billeteras.encontrar(tx.getBilleteraOrigen()).ifPresent(b -> b.registrarTransaccion(tx));
        }
        if (tx.getBilleteraDestino() != null && !tx.getBilleteraDestino().equals(tx.getBilleteraOrigen())) {
            billeteras.encontrar(tx.getBilleteraDestino()).ifPresent(b -> b.registrarTransaccion(tx));
        }
        if (tx.getUsuarioOrigenId() != null) {
            usuarios.encontrar(tx.getUsuarioOrigenId()).ifPresent(u -> u.registrarTransaccion(tx));
        }
        if (tx.getUsuarioDestinoId() != null && !tx.getUsuarioDestinoId().equals(tx.getUsuarioOrigenId())) {
            usuarios.encontrar(tx.getUsuarioDestinoId()).ifPresent(u -> u.registrarTransaccion(tx));
        }
    }

    private String siguienteId() {
        return String.format("TRX-%05d", ++contador);
    }
}
