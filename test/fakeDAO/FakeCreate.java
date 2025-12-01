package fakeDAO;

import clases.Usuario;
import interfaces.ICreate;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class FakeCreate implements ICreate<Usuario> {

    private ArrayList<Usuario> listaFake = new ArrayList<>();

    @Override
    public boolean insertar(Usuario u) {
        listaFake.add(u);
        return true;
    }
}
