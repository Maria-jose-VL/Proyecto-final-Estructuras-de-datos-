package co.edu.uniquindio.poo.fintech.servicio;

import co.edu.uniquindio.poo.fintech.estructuras.hash.TablaHash;
import co.edu.uniquindio.poo.fintech.estructuras.listas.ListaEnlazada;
import co.edu.uniquindio.poo.fintech.excepciones.UsuarioDuplicadoException;
import co.edu.uniquindio.poo.fintech.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.poo.fintech.modelo.Usuario;

import java.util.Optional;

/** Registro de usuarios. Los guarda en una tabla hash por documento. */
public class UsuarioServicio {

    private final TablaHash<String, Usuario> usuarios = new TablaHash<>();

    public Usuario registrar(String id, String nombre, String correo) throws UsuarioDuplicadoException {
        if (usuarios.containsKey(id)) {
            throw new UsuarioDuplicadoException(id);
        }
        Usuario usuario = new Usuario(id, nombre, correo);
        usuarios.put(id, usuario);
        return usuario;
    }

    public Usuario buscar(String id) throws UsuarioNoEncontradoException {
        return usuarios.get(id).orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    public Usuario modificar(String id, String nombre, String correo) throws UsuarioNoEncontradoException {
        Usuario usuario = buscar(id);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        return usuario;
    }

    public void eliminar(String id) throws UsuarioNoEncontradoException {
        buscar(id);
        usuarios.remove(id);
    }

    /** Versión sin excepción, útil cuando el usuario puede no existir. */
    public Optional<Usuario> encontrar(String id) {
        return usuarios.get(id);
    }

    public boolean existe(String id) {
        return usuarios.containsKey(id);
    }

    public ListaEnlazada<Usuario> listar() {
        return usuarios.values().ordenar(Usuario.POR_NOMBRE);
    }

    public int contar() {
        return usuarios.getSize();
    }
}
