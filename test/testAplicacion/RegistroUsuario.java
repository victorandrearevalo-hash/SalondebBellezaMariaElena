package testAplicacion;

import clases.Usuario;
import controlador.usuarioController; // asegúrate del paquete correcto
import java.util.Scanner;
import util.ValidadorSeguridad;

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
        usuarioController controller = new usuarioController();
        Usuario u = new Usuario();

        System.out.println("=== Registro de Usuario ===");

        // Solicitar datos al usuario
        System.out.print("Nombre de usuario: ");
        u.setNombre(sc.nextLine());

        System.out.print("Rol del usuario: ");
        u.setRol(sc.nextLine());

        System.out.print("Correo electrónico: ");
        u.setCorreo(sc.nextLine());

        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        // ✅ Validar seguridad de la contraseña antes de asignarla
        if (!ValidadorSeguridad.contraseñaSegura(pass)) {
            System.out.println("⚠️ La contraseña no es segura. Debe tener entre 8 y 10 caracteres, al menos una mayúscula, una minúscula, un número y un carácter especial.");
            sc.close();
            return;
        }
        u.setContrasena(pass);

        // ✅ Estado del usuario
        System.out.print("Estado del usuario (true = activo, false = inactivo): ");
        boolean estado = Boolean.parseBoolean(sc.nextLine());
        u.setEstado(estado);

        // ✅ Intentar registrar el usuario usando el Controller
        if (controller.registrarUsuario(u)) {
            System.out.println("✅ Usuario registrado correctamente.");
        } else {
            System.out.println("❌ Error al registrar usuario. Revisa la conexión o si el usuario ya existe.");
        }

        sc.close();
    }
}
