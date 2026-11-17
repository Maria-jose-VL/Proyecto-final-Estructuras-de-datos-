package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.arboles.ArbolAVL;
import co.edu.uniquindio.poo.fintech.estructuras.hash.TablaHash;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.BeneficioNoEncontradoException;
import co.edu.uniquindio.poo.fintech.excepciones.PuntosInsuficientesException;
import co.edu.uniquindio.poo.fintech.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.poo.fintech.modelo.Beneficio;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.NivelUsuario;
import co.edu.uniquindio.poo.fintech.modelo.enums.TipoTransaccion;

import java.math.BigDecimal;

/** Puntos, niveles, ranking (árbol AVL por puntos) y catálogo de beneficios (tabla hash). */
public class RecompensasServicio {

    public static final int BONO_PROGRAMADA = 10;

    private final ArbolAVL<Usuario> ranking = new ArbolAVL<>(Usuario.POR_PUNTOS);
    private final TablaHash<String, Beneficio> beneficios = new TablaHash<>();
    private final UsuarioServicio usuarioServicio;
    private final NotificacionServicio notificaciones;

    public RecompensasServicio(UsuarioServicio usuarioServicio, NotificacionServicio notificaciones) {
        this.usuarioServicio = usuarioServicio;
        this.notificaciones = notificaciones;
    }

    // ---------------- puntos ----------------

    /**
     * Parte entera de valor/100 por los puntos del tipo, más el bono de 10 si la
     * operación era programada y el bono del nivel. Una reversión no da puntos.
     */
    public int calcularPuntos(TipoTransaccion tipo, BigDecimal valor, boolean programada, NivelUsuario nivel) {
        if (tipo.getPuntosPorCien() == 0) {
            return 0;
        }
        int centenas = valor.divideToIntegralValue(BigDecimal.valueOf(100)).intValue();
        int puntos = centenas * tipo.getPuntosPorCien();
        if (programada) {
            puntos += BONO_PROGRAMADA;
        }
        return puntos + nivel.getBonoPuntos();
    }

    /** Para cambiar los puntos hay que sacar al usuario del árbol y volver a meterlo. */
    public void otorgarPuntos(Usuario usuario, int puntos) {
        quitarDelRanking(usuario);
        boolean subioDeNivel = usuario.sumarPuntos(puntos);
        registrarEnRanking(usuario);
        if (subioDeNivel) {
            notificaciones.alertarAscenso(usuario);
        }
    }

    public void descontarPuntos(Usuario usuario, int puntos) {
        quitarDelRanking(usuario);
        usuario.restarPuntos(puntos);
        registrarEnRanking(usuario);
    }

    // ---------------- ranking ----------------

    public void registrarEnRanking(Usuario usuario) {
        ranking.insertar(usuario);
    }

    public void quitarDelRanking(Usuario usuario) {
        ranking.eliminar(usuario);
    }

    /** Usuarios con puntos entre min y max, en orden ascendente. */
    public ListaEnlazada<Usuario> usuariosPorRango(int min, int max) {
        // usuarios "de prueba" con los puntos límite, para poder usar el comparador
        Usuario inferior = new Usuario("", "", "");
        inferior.sumarPuntos(min);
        Usuario superior = new Usuario("￿", "", "");
        superior.sumarPuntos(max);
        return ranking.buscarEnRango(inferior, superior);
    }

    /** Todos los usuarios del que más puntos tiene al que menos. */
    public ListaEnlazada<Usuario> ranking() {
        return ranking.obtenerMayores(ranking.getPeso());
    }

    public ListaEnlazada<Usuario> topUsuarios(int n) {
        return ranking.obtenerMayores(n);
    }

    public NivelUsuario nivelDe(Usuario usuario) {
        return usuario.getNivel();
    }

    // ---------------- beneficios ----------------

    public void registrarBeneficio(Beneficio beneficio) {
        beneficios.put(beneficio.getId(), beneficio);
    }

    /** Beneficios que un usuario de ese nivel puede ver. */
    public ListaEnlazada<Beneficio> listarBeneficios(NivelUsuario nivel) {
        return beneficios.values().filtrar(b -> b.estaDisponibleParaNivel(nivel));
    }

    public Beneficio canjear(String usuarioId, String beneficioId)
            throws UsuarioNoEncontradoException, BeneficioNoEncontradoException, PuntosInsuficientesException {
        Usuario usuario = usuarioServicio.buscar(usuarioId);
        Beneficio beneficio = (beneficioId == null) ? null : beneficios.get(beneficioId);
        if (beneficio == null) {
            throw new BeneficioNoEncontradoException("No existe el beneficio " + beneficioId);
        }
        if (!beneficio.estaDisponibleParaNivel(usuario.getNivel())) {
            throw new PuntosInsuficientesException("Este beneficio requiere nivel " + beneficio.getNivelMinimo());
        }
        if (usuario.getPuntos() < beneficio.getCostoPuntos()) {
            throw new PuntosInsuficientesException("Necesitas " + beneficio.getCostoPuntos()
                    + " puntos y tienes " + usuario.getPuntos());
        }
        descontarPuntos(usuario, beneficio.getCostoPuntos());
        usuario.agregarBeneficio(beneficio);
        notificaciones.alertarCanje(usuario, beneficio);
        return beneficio;
    }
}