package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.colas.ColaPrioridad;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.FintechException;
import co.edu.uniquindio.poo.fintech.excepciones.ProgramacionInvalidaException;
import co.edu.uniquindio.poo.fintech.excepciones.ValorInvalidoException;
import co.edu.uniquindio.poo.fintech.modelo.OperacionProgramada;
import co.edu.uniquindio.poo.fintech.modelo.Transaccion;
import co.edu.uniquindio.poo.fintech.modelo.enums.EstadoProgramada;
import co.edu.uniquindio.poo.fintech.modelo.enums.Periodicidad;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Operaciones que se ejecutan en el futuro. Las pendientes viven en una cola de prioridad
 * ordenada por fecha, así la más próxima siempre sale primero.
 */
public class ProgramadaServicio {

    private final ColaPrioridad<OperacionProgramada> pendientes = new ColaPrioridad<>(OperacionProgramada.POR_FECHA);
    private final ListaEnlazada<OperacionProgramada> registro = new ListaEnlazada<>();
    private final UsuarioServicio usuarios;
    private final TransaccionServicio transacciones;
    private final NotificacionServicio notificaciones;
    private final Clock clock;
    private int contador;

    public ProgramadaServicio(UsuarioServicio usuarios, TransaccionServicio transacciones,
                              NotificacionServicio notificaciones, Clock clock) {
        this.usuarios = usuarios;
        this.transacciones = transacciones;
        this.notificaciones = notificaciones;
        this.clock = clock;
    }

    public OperacionProgramada programar(String usuarioId, TipoTransaccion tipo, String origen, String destino,
                                         BigDecimal valor, LocalDateTime fecha, Periodicidad periodicidad)
            throws FintechException {
        usuarios.buscar(usuarioId);
        if (tipo == TipoTransaccion.REVERSION) {
            throw new ProgramacionInvalidaException("una reversión no se puede programar");
        }
        if (valor == null || valor.signum() <= 0) {
            throw new ValorInvalidoException("el valor debe ser mayor que cero");
        }
        if (fecha == null || !fecha.isAfter(LocalDateTime.now(clock))) {
            throw new ProgramacionInvalidaException("la fecha debe ser posterior a este momento");
        }
        OperacionProgramada operacion = new OperacionProgramada(siguienteId(), usuarioId, tipo,
                origen, destino, valor, fecha, periodicidad);
        pendientes.encolar(operacion);
        registro.addLast(operacion);
        notificaciones.alertarProgramada(usuarioId, "Programaste " + operacion.getId() + " para " + fecha);
        return operacion;
    }

    /**
     * Ejecuta todas las operaciones vencidas, en orden de fecha. Si una falla, queda marcada
     * como fallida y se avisa al usuario. Las periódicas generan la siguiente ejecución.
     */
    public ListaEnlazada<Transaccion> ejecutarVencidas() {
        LocalDateTime ahora = LocalDateTime.now(clock);
        ListaEnlazada<Transaccion> ejecutadas = new ListaEnlazada<>();
        while (!pendientes.isEmpty() && pendientes.verPrimero().estaVencida(ahora)) {
            OperacionProgramada operacion = pendientes.desencolar();
            try {
                Transaccion tx = transacciones.ejecutar(operacion.getTipo(), operacion.getUsuarioId(),
                        operacion.getBilleteraOrigen(), operacion.getBilleteraDestino(),
                        operacion.getValor(), true);
                operacion.marcarEjecutada();
                ejecutadas.addLast(tx);
                notificaciones.alertarProgramada(operacion.getUsuarioId(), "Se ejecutó " + operacion.getId());
            } catch (FintechException e) {
                operacion.marcarFallida();
                notificarFallo(operacion, e);
            }
            if (operacion.getPeriodicidad() != Periodicidad.UNICA) {
                OperacionProgramada siguiente = operacion.crearSiguiente(siguienteId());
                pendientes.encolar(siguiente);
                registro.addLast(siguiente);
            }
        }
        return ejecutadas;
    }

    public void cancelar(String id) throws ProgramacionInvalidaException {
        OperacionProgramada operacion = buscar(id)
                .orElseThrow(() -> new ProgramacionInvalidaException("no existe la programación " + id));
        if (operacion.getEstado() != EstadoProgramada.PENDIENTE) {
            throw new ProgramacionInvalidaException("la programación " + id + " ya está " + operacion.getEstado());
        }
        pendientes.eliminar(operacion);
        operacion.cancelar();
    }

    public ListaEnlazada<OperacionProgramada> listarDe(String usuarioId) {
        return registro.filtrar(o -> o.getUsuarioId().equals(usuarioId));
    }

    public ListaEnlazada<OperacionProgramada> listarTodas() {
        return registro.filtrar(o -> true);
    }

    public int cantidadPendientes() {
        return pendientes.getSize();
    }

    private Optional<OperacionProgramada> buscar(String id) {
        for (OperacionProgramada o : registro) {
            if (o.getId().equals(id)) {
                return Optional.of(o);
            }
        }
        return Optional.empty();
    }

    private void notificarFallo(OperacionProgramada operacion, FintechException causa) {
        try {
            notificaciones.alertarRechazo(operacion.getUsuarioId(),
                    "la programación " + operacion.getId() + " falló: " + causa.getMessage());
        } catch (FintechException ignorada) {
            // El usuario ya no existe; la operación igual queda marcada como fallida.
        }
    }

    private String siguienteId() {
        return String.format("PRG-%04d", ++contador);
    }
}
