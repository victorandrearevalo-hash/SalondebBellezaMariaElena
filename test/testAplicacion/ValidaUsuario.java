package testAplicacion;

import clases.Usuario;
import controlador.UsuarioController; 

/**
 *
 * @author Administrador
 */
public class ValidaUsuario {

    public static void main(String[] args) {
        UsuarioController controller = new UsuarioController();
        Usuario user = controller.login("Admin", "123456");

        if (user != null) {
            System.out.println("✅ Login correcto: " + user.getNombreUsuario());
        } else {
            System.out.println("❌ Usuario o contraseña incorrectos");
        }
    }

}
