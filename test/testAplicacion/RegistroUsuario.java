package testAplicacion;

import clases.Usuario;
import controlador.UsuarioController; // asegúrate del paquete correcto
import java.util.Scanner;

/**
 *
 * @author Administrador
 *
 */
public class RegistroUsuario {

    /**
     * Programa que valide si una contraseña especificada por el usuario es segura. Una contraseña segura: - Tiene más de 8 caracteres - Tiene al menos una letra mayúscula - Tiene al menos un número
     *
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuarioController controller = new UsuarioController();
        Usuario u = new Usuario();

        System.out.println("=== Registro de Usuario ===");

        // Solicitar datos al usuario
        System.out.print("Nombre de usuario: ");
        u.setNombreUsuario(sc.nextLine());

        System.out.print("Rol del usuario: ");
        u.setRolUsuario(sc.nextLine());

        System.out.print("Correo electrónico: ");
        u.setCorreoUsuario(sc.nextLine());

        System.out.print("Contraseña: ");
        u.setPasswordUsuario(sc.nextLine());

        // Validar seguridad de la contraseña
        if (!controller.esContrasenaSegura(u.getPasswordUsuario())) {
            System.out.println("⚠️ La contraseña no es segura. Debe tener más de 8 caracteres, al menos una mayúscula y un número.");
            return;
        }

        // Intentar registrar el usuario usando el DAO y SP
        if (controller.registrarUsuario(u)) {
            System.out.println("✅ Usuario registrado correctamente.");
        } else {
            System.out.println("❌ Error al registrar usuario. Revisa la conexión o si el usuario ya existe.");
        }

        // Cerrar el scanner
        sc.close();
    }
}
