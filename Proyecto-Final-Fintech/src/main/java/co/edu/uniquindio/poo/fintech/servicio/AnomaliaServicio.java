package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.poo.fintech.modelo.EventoAuditoria;
import co.edu.uniquindio.poo.fintech.modelo.Transaccion;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.EstadoTransaccion;
import co.edu.uniquindio.poo.fintech.modelo.enums.NivelRiesgo;
import co.edu.uniquindio.poo.fintech.modelo.enums.ReglaAnomalia;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

/**
 * Revisa cada operación contra las seis reglas del enunciado y guarda un evento de auditoría
 * por cada regla que se cumple.
 *
 * Los umbrales de RAFAGA, MONTO_ATIPICO y DESTINO_REPETIDO vienen del enunciado.
 * Los de FRAGMENTACION, CAMBIO_FRECUENCIA y HORARIO_INUSUAL son propuestos por el equipo
 * y hay que confirmarlos con el docente.
 */
public class AnomaliaServicio {

    private static final int RAFAGA_MAXIMA = 4;
    private static final Duration VENTANA_RAFAGA = Duration.ofMinutes(1);
    private static final Duration VENTANA_REPETIDO = Duration.ofMinutes(5);
    private static final BigDecimal FACTOR_MONTO_ATIPICO = new BigDecimal("3");

    private static final Duration VENTANA_FRAGMENTACION = Duration.ofMinutes(10);
    private static final BigDecimal MONTO_FRAGMENTO = new BigDecimal("1000");
    private static final int OPERACIONES_FRAGMENTACION = 3;

    private static final int OPERACIONES_FRECUENCIA_MINIMAS = 5;
    private static final int FACTOR_FRECUENCIA = 3;

    private static final int HORA_NOCTURNA_FIN = 5;

    private record Hallazgo(ReglaAnomalia regla, NivelRiesgo nivel, String detalle) {
    }

    private final ListaEnlazada<EventoAuditoria> eventos = new ListaEnlazada<>();
    private final NotificacionServicio notificaciones;
    private int contador;

    public AnomaliaServicio(NotificacionServicio notificaciones) {
        this.notificaciones = notificaciones;
    }

    /**
     * Evalúa la operación. Debe llamarse después de registrarla en el historial del actor.
     * Devuelve el nivel de riesgo más alto de las reglas que se cumplieron.
     */
    public NivelRiesgo evaluar(Transaccion tx, Usuario actor) throws UsuarioNoEncontradoException {
        ListaEnlazada<Transaccion> previas = operacionesPrevias(actor, tx);
        ListaEnlazada<Hallazgo> hallazgos = new ListaEnlazada<>();
        agregar(hallazgos, rafaga(tx, previas));
        agregar(hallazgos, montoAtipico(tx, previas));
        agregar(hallazgos, destinoRepetido(tx, previas));
        agregar(hallazgos, fragmentacion(tx, previas));
        agregar(hallazgos, cambioFrecuencia(tx, previas));
        agregar(hallazgos, horarioInusual(tx));

        NivelRiesgo riesgo = NivelRiesgo.BAJO;
        for (Hallazgo h : hallazgos) {
            eventos.addLast(new EventoAuditoria("AUD-" + (++contador), tx.getFecha(), tx.getId(),
                    actor.getId(), h.regla(), h.nivel(), h.detalle()));
            if (h.nivel().ordinal() > riesgo.ordinal()) {
                riesgo = h.nivel();
            }
        }
        tx.setNivelRiesgo(riesgo);
        if (riesgo == NivelRiesgo.ALTO) {
            notificaciones.alertarSeguridad(actor.getId(),
                    "La operación " + tx.getId() + " se marcó con riesgo alto");
        }
        return riesgo;
    }

    public ListaEnlazada<EventoAuditoria> eventos() {
        return eventos.filtrar(e -> true);
    }

    public ListaEnlazada<EventoAuditoria> eventosDe(String usuarioId) {
        return eventos.filtrar(e -> e.getUsuarioId().equals(usuarioId));
    }

    private static ListaEnlazada<Transaccion> operacionesPrevias(Usuario actor, Transaccion actual) {
        return actor.getHistorial().filtrar(t -> t != actual
                && t.getEstado() != EstadoTransaccion.RECHAZADA
                && esDeActor(t, actor.getId()));
    }

    /** Una operación es del actor si él la originó, o si es una recarga a su favor. */
    private static boolean esDeActor(Transaccion t, String id) {
        if (t.getUsuarioOrigenId() != null) {
            return id.equals(t.getUsuarioOrigenId());
        }
        return id.equals(t.getUsuarioDestinoId());
    }

    private static Hallazgo rafaga(Transaccion tx, ListaEnlazada<Transaccion> previas) {
        LocalDateTime desde = tx.getFecha().minus(VENTANA_RAFAGA);
        int cantidad = 1 + contarDesde(previas, desde, t -> true);
        if (cantidad < RAFAGA_MAXIMA) {
            return null;
        }
        return new Hallazgo(ReglaAnomalia.RAFAGA, NivelRiesgo.MEDIO,
                cantidad + " operaciones en menos de un minuto");
    }

    private static Hallazgo montoAtipico(Transaccion tx, ListaEnlazada<Transaccion> previas) {
        BigDecimal suma = BigDecimal.ZERO;
        int cantidad = 0;
        for (Transaccion t : previas) {
            if (t.getEstado() == EstadoTransaccion.EXITOSA) {
                suma = suma.add(t.getValor());
                cantidad++;
            }
        }
        if (cantidad == 0) {
            return null;
        }
        BigDecimal promedio = suma.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP);
        if (tx.getValor().compareTo(promedio.multiply(FACTOR_MONTO_ATIPICO)) <= 0) {
            return null;
        }
        return new Hallazgo(ReglaAnomalia.MONTO_ATIPICO, NivelRiesgo.ALTO,
                "monto " + tx.getValor() + " mayor que 3 veces el promedio " + promedio);
    }

    private static Hallazgo destinoRepetido(Transaccion tx, ListaEnlazada<Transaccion> previas) {
        if (tx.getTipo() != TipoTransaccion.TRANSFERENCIA) {
            return null;
        }
        String destino = tx.getUsuarioDestinoId();
        LocalDateTime desde = tx.getFecha().minus(VENTANA_REPETIDO);
        int repetidas = contarDesde(previas, desde,
                t -> t.getTipo() == TipoTransaccion.TRANSFERENCIA && destino.equals(t.getUsuarioDestinoId()));
        if (repetidas < 1) {
            return null;
        }
        return new Hallazgo(ReglaAnomalia.DESTINO_REPETIDO, NivelRiesgo.MEDIO,
                "dos transferencias al mismo destino en cinco minutos");
    }

    private static Hallazgo fragmentacion(Transaccion tx, ListaEnlazada<Transaccion> previas) {
        LocalDateTime desde = tx.getFecha().minus(VENTANA_FRAGMENTACION);
        int pequenas = contarDesde(previas, desde, t -> t.getValor().compareTo(MONTO_FRAGMENTO) < 0);
        if (tx.getValor().compareTo(MONTO_FRAGMENTO) < 0) {
            pequenas++;
        }
        if (pequenas < OPERACIONES_FRAGMENTACION) {
            return null;
        }
        return new Hallazgo(ReglaAnomalia.FRAGMENTACION, NivelRiesgo.BAJO,
                pequenas + " operaciones menores a " + MONTO_FRAGMENTO + " en diez minutos");
    }

    /** Compara las operaciones de hoy con el promedio diario de los siete días anteriores. */
    private static Hallazgo cambioFrecuencia(Transaccion tx, ListaEnlazada<Transaccion> previas) {
        LocalDateTime inicioHoy = tx.getFecha().minusHours(24);
        LocalDateTime inicioSemana = tx.getFecha().minusDays(8);
        int hoy = 1 + contarDesde(previas, inicioHoy, t -> true);
        int semana = contarDesde(previas, inicioSemana, t -> t.getFecha().isBefore(inicioHoy));
        double promedioDiario = semana / 7.0;
        if (hoy < OPERACIONES_FRECUENCIA_MINIMAS || hoy <= FACTOR_FRECUENCIA * promedioDiario) {
            return null;
        }
        return new Hallazgo(ReglaAnomalia.CAMBIO_FRECUENCIA, NivelRiesgo.BAJO,
                hoy + " operaciones hoy frente a un promedio de " + String.format("%.1f", promedioDiario) + " por día");
    }

    private static Hallazgo horarioInusual(Transaccion tx) {
        if (tx.getFecha().getHour() > HORA_NOCTURNA_FIN) {
            return null;
        }
        return new Hallazgo(ReglaAnomalia.HORARIO_INUSUAL, NivelRiesgo.BAJO,
                "operación entre las 00:00 y las 05:59");
    }

    private static int contarDesde(ListaEnlazada<Transaccion> lista, LocalDateTime desde,
                                   Predicate<Transaccion> condicion) {
        int cantidad = 0;
        for (Transaccion t : lista) {
            if (!t.getFecha().isBefore(desde) && condicion.test(t)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private static void agregar(ListaEnlazada<Hallazgo> lista, Hallazgo hallazgo) {
        if (hallazgo != null) {
            lista.addLast(hallazgo);
        }
    }
}