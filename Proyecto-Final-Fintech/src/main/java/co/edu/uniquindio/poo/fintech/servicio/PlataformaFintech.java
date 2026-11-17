package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.excepciones.FintechException;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;

import java.time.Clock;

/**
 * Fachada del sistema. Crea los servicios en el orden de sus dependencias
 * y es el único punto de entrada que usa la interfaz gráfica.
 */
public class PlataformaFintech {

    private final UsuarioServicio usuarios;
    private final BilleteraServicio billeteras;
    private final NotificacionServicio notificaciones;
    private final RecompensasServicio recompensas;
    private final AnomaliaServicio anomalias;
    private final TransaccionServicio transacciones;
    private final ProgramadaServicio programadas;
    private final ReporteServicio reportes;

    public PlataformaFintech() {
        this(Clock.systemDefaultZone());
    }

    /** El reloj se recibe por parámetro para poder probar las fechas sin depender de la hora real. */
    public PlataformaFintech(Clock clock) {
        usuarios = new UsuarioServicio();
        billeteras = new BilleteraServicio(usuarios);
        notificaciones = new NotificacionServicio(usuarios, clock);
        recompensas = new RecompensasServicio(usuarios, notificaciones, clock);
        anomalias = new AnomaliaServicio(notificaciones);
        transacciones = new TransaccionServicio(usuarios, billeteras, recompensas, anomalias, notificaciones, clock);
        programadas = new ProgramadaServicio(usuarios, transacciones, notificaciones, clock);
        reportes = new ReporteServicio(transacciones, recompensas);
    }

    public Usuario registrarUsuario(String id, String nombre, String correo) throws FintechException {
        Usuario usuario = usuarios.registrar(id, nombre, correo);
        recompensas.registrarEnRanking(usuario);
        transacciones.registrarVertice(id);
        return usuario;
    }

    public void eliminarUsuario(String id) throws FintechException {
        Usuario usuario = usuarios.buscar(id);
        recompensas.quitarDelRanking(usuario);
        billeteras.eliminarDeUsuario(id);
        usuarios.eliminar(id);
    }

    public UsuarioServicio usuarios() {
        return usuarios;
    }

    public BilleteraServicio billeteras() {
        return billeteras;
    }

    public NotificacionServicio notificaciones() {
        return notificaciones;
    }

    public RecompensasServicio recompensas() {
        return recompensas;
    }

    public AnomaliaServicio anomalias() {
        return anomalias;
    }

    public TransaccionServicio transacciones() {
        return transacciones;
    }

    public ProgramadaServicio programadas() {
        return programadas;
    }

    public ReporteServicio reportes() {
        return reportes;
    }
}
