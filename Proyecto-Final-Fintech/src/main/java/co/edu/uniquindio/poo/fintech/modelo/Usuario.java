import co.edu.uniquindio.poo.fintech.estructuras.colas.Cola;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.estructuras.pilas.Pila;
import co.edu.uniquindio.poo.fintech.modelo.Billetera;
import co.edu.uniquindio.poo.fintech.modelo.Canje;
import co.edu.uniquindio.poo.fintech.modelo.Notificacion;
import co.edu.uniquindio.poo.fintech.modelo.Transaccion;
import co.edu.uniquindio.poo.fintech.modelo.enums.NivelUsuario;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;{

/** Mayor número de puntos primero; si empatan, orden por documento. */
public static final Comparator<Usuario> POR_PUNTOS =
        Comparator.comparingInt(Usuario::getPuntos).reversed().thenComparing(Usuario::getId);

public static final Comparator<Usuario> POR_NOMBRE =
        Comparator.comparing(Usuario::getNombre).thenComparing(Usuario::getId);

private static final int LIMITE_NOTIFICACIONES = 20;

private final String id;
private String nombre;
private String correo;
private int puntos;
private NivelUsuario nivel;
private final ListaEnlazada<Billetera> billeteras = new ListaEnlazada<>();
private final ListaEnlazada<Transaccion> historial = new ListaEnlazada<>();
private final Pila<Transaccion> pilaDeshacer = new Pila<>();
private final Cola<Notificacion> notificaciones = new Cola<>(LIMITE_NOTIFICACIONES);
private final ListaEnlazada<Canje> canjes = new ListaEnlazada<>();

public Usuario(String id, String nombre, String correo) {
    this.id = Objects.requireNonNull(id, "El documento no puede ser nulo");
    this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
    this.correo = correo;
    this.puntos = 0;
    this.nivel = NivelUsuario.BRONCE;
}

public void agregarBilletera(Billetera billetera) {
    billeteras.addLast(billetera);
}

public Optional<Billetera> buscarBilletera(String codigo) {
    for (Billetera b : billeteras) {
        if (b.getCodigo().equals(codigo)) {
            return Optional.of(b);
        }
    }
    return Optional.empty();
}

public boolean eliminarBilletera(String codigo) {
    return billeteras.removeIf(b -> b.getCodigo().equals(codigo)) > 0;
}

public void registrarTransaccion(Transaccion transaccion) {
    historial.addLast(transaccion);
}

public void apilarParaDeshacer(Transaccion transaccion) {
    pilaDeshacer.push(transaccion);
}

/** Saca la última transacción de la pila de deshacer. */
public Transaccion desapilarUltima() {
    return pilaDeshacer.pop();
}

public Transaccion verUltimaReversible() {
    return pilaDeshacer.peek();
}

public boolean tieneReversibles() {
    return !pilaDeshacer.isEmpty();
}

public void agregarNotificacion(Notificacion notificacion) {
    notificaciones.encolarDescartandoAntiguo(notificacion);
}

public void registrarCanje(Canje canje) {
    canjes.addLast(canje);
}

/** Suma puntos. Devuelve true si el usuario subió de nivel con esta suma. */
public boolean sumarPuntos(int cantidad) {
    NivelUsuario anterior = nivel;
    puntos += cantidad;
    nivel = NivelUsuario.dePuntos(puntos);
    return nivel.ordinal() > anterior.ordinal();
}

/** Resta puntos sin bajar de cero. Actualiza el nivel. */
public void restarPuntos(int cantidad) {
    puntos = Math.max(0, puntos - cantidad);
    nivel = NivelUsuario.dePuntos(puntos);
}

public BigDecimal calcularSaldoTotal() {
    BigDecimal total = BigDecimal.ZERO;
    for (Billetera b : billeteras) {
        total = total.add(b.getSaldo());
    }
    return total.setScale(2, java.math.RoundingMode.HALF_UP);
}

public String getId() {
    return id;
}

public String getNombre() {
    return nombre;
}

public void setNombre(String nombre) {
    this.nombre = Objects.requireNonNull(nombre);
}

public String getCorreo() {
    return correo;
}

public void setCorreo(String correo) {
    this.correo = correo;
}

public int getPuntos() {
    return puntos;
}

public NivelUsuario getNivel() {
    return nivel;
}

public ListaEnlazada<Billetera> getBilleteras() {
    return billeteras;
}

public ListaEnlazada<Transaccion> getHistorial() {
    return historial;
}

public Cola<Notificacion> getNotificaciones() {
    return notificaciones;
}

public ListaEnlazada<Canje> getCanjes() {
    return canjes;
}

@Override
public boolean equals(Object o) {
    return this == o || (o instanceof Usuario u && id.equals(u.id));
}

@Override
public int hashCode() {
    return id.hashCode();
}

@Override
public String toString() {
    return nombre + " (" + id + ") - " + puntos + " pts, nivel " + nivel;
}
}