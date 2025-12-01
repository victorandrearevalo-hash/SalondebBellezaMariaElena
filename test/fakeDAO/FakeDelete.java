package fakeDAO;

import clases.Usuario;
import interfaces.IDelete;

/**
 *
 * @author Administrador
 */
public class FakeDelete implements IDelete<Usuario> {

    @Override
    public boolean eliminar(Usuario u) {
        System.out.println("FakeDelete: eliminación simulada de " + u.getNombre());
        return true;
    }
}
