package clases;

/**
 *
 * @author Administrador
 */
public class SesionUsuario {

    private static Usuario usuarioActual;

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static int getIdUsuarioActual() {
        return (usuarioActual != null) ? usuarioActual.getId() : 0;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

}
