package co.edu.uniquindio.poo.fintech.rendimiento;

/** Tiempo que tomó una operación sobre una estructura con n datos. */
public record ResultadoMedicion(String estructura, String operacion, int n, long nanosegundos) {

    public double milisegundos() {
        return nanosegundos / 1_000_000.0;
    }
}
