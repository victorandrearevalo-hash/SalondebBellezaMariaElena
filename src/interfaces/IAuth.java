package interfaces;

import clases.Usuario;

/**
 *
 * @author Administrador
 */
public interface IAuth {

    Usuario validarLogin(String nombreUsuario, String password);
}
