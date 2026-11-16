package co.edu.uniquindio.poo.fintech.modelo;

import co.edu.uniquindio.poo.fintech.modelo.enums.NivelUsuario;

import java.util.Objects;

/** Premio del catálogo que se puede canjear con puntos. */
public class Beneficio {

    private final String id;
    private final String nombre;
    private final String descripcion;
    private final int costoPuntos;
    private final NivelUsuario nivelMinimo;

    public Beneficio(String id, String nombre, String descripcion, int costoPuntos, NivelUsuario nivelMinimo) {
        this.id = Objects.requireNonNull(id);
        this.nombre = Objects.requireNonNull(nombre);
        this.descripcion = descripcion;
        this.costoPuntos = costoPuntos;
        this.nivelMinimo = Objects.requireNonNull(nivelMinimo);
    }

    public boolean estaDisponibleParaNivel(NivelUsuario nivel) {
        return nivel.ordinal() >= nivelMinimo.ordinal();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCostoPuntos() {
        return costoPuntos;
    }

    public NivelUsuario getNivelMinimo() {
        return nivelMinimo;
    }

    @Override
    public String toString() {
        return nombre + " (" + costoPuntos + " pts, desde " + nivelMinimo + ")";
    }
}