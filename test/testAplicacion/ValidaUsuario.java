package testAplicacion;

import clases.Usuario;
import controlador.usuarioController; // Asegúrate de que el paquete coincida con el tuyo

/**
 *
 * @author Administrador
 */
public class ValidaUsuario {

    public static void main(String[] args) {
        usuarioController controller = new usuarioController();

        boolean loginExitoso = controller.validarlogin("Administrador", "Admin$1234");

        String msj = controller.getMensaje();
        int tipo = controller.getTipoMensaje();

        switch (tipo) {
            case 0:
                System.out.println(msj + " Error");
                break;
            case 1:
                System.out.println(msj + " Advertencia");
                break;
            case 2:
                Usuario user = controller.getUsuarioActual();
                if (user != null) {
                    System.out.println(msj);
                    System.out.println("✅ Usuario autenticado correctamente (" +  user.getRol() + ")");

                } else {
                    System.out.println(msj + " Usuario no disponible.");
                }
                break;
            default:
                System.out.println("❌ Tipo de mensaje desconocido: " + msj);
                break;
        }

    }
}
