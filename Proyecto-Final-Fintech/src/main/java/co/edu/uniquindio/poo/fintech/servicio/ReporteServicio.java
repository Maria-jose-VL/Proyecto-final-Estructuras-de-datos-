package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.grafos.Grafo;
import co.edu.uniquindio.poo.fintech.estructuras.hash.TablaHash;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.modelo.Canje;
import co.edu.uniquindio.poo.fintech.modelo.Transaccion;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;

import java.time.LocalDateTime;

/** Consultas de análisis que combinan las estructuras de los demás servicios. */
public class ReporteServicio {

    private final TransaccionServicio transacciones;
    private final RecompensasServicio recompensas;

    public ReporteServicio(TransaccionServicio transacciones, RecompensasServicio recompensas) {
        this.transacciones = transacciones;
        this.recompensas = recompensas;
    }

    /** Cuántas transacciones hay de cada tipo. Usa una tabla hash como acumulador. */
    public TablaHash<TipoTransaccion, Integer> conteoPorTipo() {
        TablaHash<TipoTransaccion, Integer> conteo = new TablaHash<>();
        for (Transaccion t : transacciones.todas()) {
            conteo.put(t.getTipo(), conteo.get(t.getTipo()).orElse(0) + 1);
        }
        return conteo;
    }

    public ListaEnlazada<Transaccion> mayoresTransacciones(int n) {
        return transacciones.mayores(n);
    }

    /** Las n relaciones entre usuarios con más transferencias. */
    public ListaEnlazada<Grafo.Arco<String>> relacionesMasFrecuentes(int n) {
        return transacciones.relaciones().aristasMasFrecuentes(n);
    }

    public ListaEnlazada<ListaEnlazada<String>> ciclosDeRelaciones() {
        return transacciones.relaciones().encontrarCiclos();
    }

    public ListaEnlazada<Usuario> topPuntos(int n) {
        return recompensas.topUsuarios(n);
    }

    public ListaEnlazada<Canje> canjesEntre(LocalDateTime desde, LocalDateTime hasta) {
        return recompensas.historialCanjes(desde, hasta);
    }

    public ListaEnlazada<Transaccion> operacionesEntre(LocalDateTime desde, LocalDateTime hasta) {
        return transacciones.todas().filtrar(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta));
    }
}
