package fakeDAO;

import clases.Usuario;
import interfaces.IAuth;

/**
 *
 * @author Administrador
 */
public class FakeAuth implements IAuth {

    @Override
    public Usuario validarLogin(String user, String pass) {

        if (user.equals("Administrador") && pass.equals("Admin$1234")) {
            Usuario u = new Usuario();
            u.setNombre("Administrador");
            u.setRol("Admin Completo");
            return u;
        }

        return null; // Login fallido
    }
}
