package fakeDAO;

import clases.Usuario;
import interfaces.IUpdate;

/**
 *
 * @author Administrador
 */
public class FakeUpdate implements IUpdate<Usuario> {

    @Override
    public boolean actualizar(Usuario u) {
        System.out.println("FakeUpdate: actualización simulada de " + u.getNombre());
        return true; // Siempre exitoso
    }
}
