package testAplicacion;

import clases.Usuario;
import controlador.usuarioController;
import java.util.Scanner;
import util.ValidadorSeguridad;

/**
 *
 * @author Administrador
 */
public class ActualizarUsuario {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        usuarioController controller = new usuarioController();
        Usuario u = new Usuario();

        System.out.println("=== Actualizar Usuario ===");

        System.out.print("ID del usuario a actualizar: ");
        u.setId(sc.nextInt());
        sc.nextLine(); // limpiar buffer

        System.out.print("Nuevo nombre: ");
        u.setNombre(sc.nextLine());

        System.out.print("Nuevo rol: ");
        u.setRol(sc.nextLine());

        System.out.print("Nuevo correo: ");
        u.setCorreo(sc.nextLine());

        System.out.print("¿Deseas cambiar la contraseña? (s/n): ");
        String opcion = sc.nextLine();

        // ✅ Solo pedimos y validamos si el usuario dice “s”
        if (opcion.equalsIgnoreCase("s")) {
            System.out.print("Nueva contraseña: ");
            String nuevaPass = sc.nextLine().trim();

            // Validar seguridad SOLO si escribió algo
            if (nuevaPass.isEmpty()) {
                System.out.println("⚠️ No se ingresó ninguna contraseña. No se actualizará la contraseña.");
                u.setContrasena(null);
            } else if (!ValidadorSeguridad.contraseñaSegura(nuevaPass)) {
                System.out.println("⚠️ La contraseña no es segura. Debe tener entre 8 y 10 caracteres, al menos una mayúscula, una minúscula, un número y un carácter especial.");
                sc.close();
                return;
            } else {
                u.setContrasena(nuevaPass);
            }
        } else {
            // ❌ No desea cambiar contraseña
            u.setContrasena(null);
        }

        System.out.print("Estado del usuario (true = activo, false = inactivo): ");
        boolean estado = Boolean.parseBoolean(sc.nextLine());
        u.setEstado(estado);

        // ✅ Llamar al controlador
        if (controller.actualizarUsuario(u)) {
            System.out.println("✅ Usuario actualizado correctamente.");
        } else {
            System.out.println("❌ Error al actualizar usuario.");
        }

        sc.close();

    }
}
