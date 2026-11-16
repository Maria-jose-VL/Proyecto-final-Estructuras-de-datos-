package co.edu.uniquindio.poo.fintech.modelo.enums;

import java.time.LocalDateTime;

public enum Periodicidad {
    UNICA,
    DIARIA,
    SEMANAL,
    MENSUAL;

    /** Fecha de la siguiente ejecución, o null si la operación es única. */
    public LocalDateTime siguienteFecha(LocalDateTime fechaActual) {
        return switch (this) {
            case UNICA -> null;
            case DIARIA -> fechaActual.plusDays(1);
            case SEMANAL -> fechaActual.plusWeeks(1);
            case MENSUAL -> fechaActual.plusMonths(1);
        };
    }
}
