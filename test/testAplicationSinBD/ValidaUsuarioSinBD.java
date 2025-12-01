package testAplicationSinBD;

import clases.Usuario;
import controlador.usuarioController;
import fakeDAO.FakeUsuarioDAO;
import java.util.Scanner;

/**
 *
 * @author Administrador
 */
public class ValidaUsuarioSinBD {

    public static void main(String[] args) {

        FakeUsuarioDAO fake = new FakeUsuarioDAO();

        usuarioController controller = new usuarioController(
                fake, fake, fake, fake, fake
        );

        Scanner sc = new Scanner(System.in);
        Usuario u = new Usuario();

        System.out.println("=== Registro de Usuario (SIN BD) ===");

        System.out.print("Nombre: ");
        u.setNombre(sc.nextLine());

        System.out.print("Rol: ");
        u.setRol(sc.nextLine());

        System.out.print("Correo: ");
        u.setCorreo(sc.nextLine());

        System.out.print("Contraseña: ");
        u.setContrasena(sc.nextLine());

        System.out.print("Estado: ");
        u.setEstado(Boolean.parseBoolean(sc.nextLine()));

        if (controller.registrarUsuario(u)) {
            System.out.println("✔ Registrado sin BD");
        } else {
            System.out.println("❌ Error: " + controller.getMensaje());
        }

        sc.close();
    }
}
