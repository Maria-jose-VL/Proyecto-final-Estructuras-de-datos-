package co.edu.uniquindio.poo.fintech.modelo.enums;

public enum TipoBilletera {
    AHORRO("Ahorro"),
    GASTOS_DIARIOS("Gastos diarios"),
    COMPRAS("Compras"),
    TRANSPORTE("Transporte"),
    INVERSION("Inversión"),
    SUSCRIPCIONES("Suscripciones");

    private final String nombre;

    TipoBilletera(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
