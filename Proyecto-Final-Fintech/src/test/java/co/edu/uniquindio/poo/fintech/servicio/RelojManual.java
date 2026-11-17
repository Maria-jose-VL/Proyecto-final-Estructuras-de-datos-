package co.edu.uniquindio.poo.fintech.servicio;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Reloj que el test puede mover a mano, para probar fechas sin depender de la hora real. */
class RelojManual extends Clock {

    private final ZoneId zona = ZoneId.systemDefault();
    private Instant instante;

    RelojManual(LocalDateTime fecha) {
        this.instante = fecha.atZone(zona).toInstant();
    }

    void avanzarHasta(LocalDateTime fecha) {
        this.instante = fecha.atZone(zona).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId otraZona) {
        return this;
    }

    @Override
    public Instant instant() {
        return instante;
    }
}
