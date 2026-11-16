package co.edu.uniquindio.poo.fintech.modelo.enums;

import java.math.BigDecimal;

/** Niveles según los puntos acumulados. Los rangos son inclusivos. */
public enum NivelUsuario {
    BRONCE(0, 500, new BigDecimal("1000.00"), 0),
    PLATA(501, 1000, new BigDecimal("5000.00"), 5),
    ORO(1001, 5000, new BigDecimal("20000.00"), 10),
    PLATINO(5001, Integer.MAX_VALUE, new BigDecimal("100000.00"), 20);

    private final int puntosMinimos;
    private final int puntosMaximos;
    private final BigDecimal limiteDiario;
    private final int bonoPuntos;

    NivelUsuario(int puntosMinimos, int puntosMaximos, BigDecimal limiteDiario, int bonoPuntos) {
        this.puntosMinimos = puntosMinimos;
        this.puntosMaximos = puntosMaximos;
        this.limiteDiario = limiteDiario;
        this.bonoPuntos = bonoPuntos;
    }

    public static NivelUsuario dePuntos(int puntos) {
        for (NivelUsuario nivel : values()) {
            if (puntos >= nivel.puntosMinimos && puntos <= nivel.puntosMaximos) {
                return nivel;
            }
        }
        return puntos < 0 ? BRONCE : PLATINO;
    }

    public BigDecimal getLimiteDiario() {
        return limiteDiario;
    }

    /** Puntos extra por cada operación que realiza el usuario en este nivel. */
    public int getBonoPuntos() {
        return bonoPuntos;
    }

    public int getPuntosMinimos() {
        return puntosMinimos;
    }
}
