package co.edu.uniquindio.poo.fintech.modelo.enums;

/** Cada tipo tiene los puntos que da por cada 100 unidades de valor. */
public enum TipoTransaccion {
    RECARGA(1),
    RETIRO(2),
    TRANSFERENCIA(3),
    REVERSION(0);

    private final int puntosPorCien;

    TipoTransaccion(int puntosPorCien) {
        this.puntosPorCien = puntosPorCien;
    }

    public int getPuntosPorCien() {
        return puntosPorCien;
    }
}