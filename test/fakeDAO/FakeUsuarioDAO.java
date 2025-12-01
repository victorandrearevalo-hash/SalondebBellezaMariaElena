package fakeDAO;

import clases.Usuario;
import interfaces.*;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class FakeUsuarioDAO implements IRead<Usuario>, ICreate<Usuario>,
        IUpdate<Usuario>, IDelete<Usuario>, IAuth {

    private ArrayList<Usuario> lista = new ArrayList<>();

    // Mensajes simulados
    private String ultimoMensaje = "";
    private int ultimoTipo = 0;

    // ---- CREATE ----
    @Override
    public boolean insertar(Usuario u) {
        lista.add(u);
        ultimoMensaje = "Usuario insertado (FAKE)";
        ultimoTipo = 2;
        return true;
    }

    // ---- READ ----
    @Override
    public ArrayList<Usuario> listar() {
        return lista;
    }

    @Override
    public ArrayList<Usuario> buscar(String criterio) {
        ArrayList<Usuario> resultado = new ArrayList<>();
        for (Usuario u : lista) {
            if (u.getNombre().contains(criterio)) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    // ---- UPDATE ----
    @Override
    public boolean actualizar(Usuario u) {
        ultimoMensaje = "Actualización FAKE exitosa";
        ultimoTipo = 2;
        return true;
    }

    // ---- DELETE ----
    @Override
    public boolean eliminar(Usuario u) {
        lista.remove(u);
        ultimoMensaje = "Eliminado FAKE";
        ultimoTipo = 2;
        return true;
    }

    // ---- AUTH ----
    @Override
    public Usuario validarLogin(String user, String pass) {
        for (Usuario u : lista) {
            if (u.getNombre().equals(user) && u.getContrasena().equals(pass)) {
                return u;
            }
        }
        return null;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public int getUltimoTipo() {
        return ultimoTipo;
    }
}
