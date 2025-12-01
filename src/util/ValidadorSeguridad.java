package util;

/**
 *
 * @author Administrador
 */
public class ValidadorSeguridad {

    public static boolean contraseñaSegura(String password) {

        if (password == null || password.isBlank()) {
            return false;
        }

        // Requisitos
        boolean longitudValida = password.length() >= 8 && password.length() <= 10;
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneMinuscula = password.matches(".*[a-z].*");
        boolean tieneNumero = password.matches(".*\\d.*");
        boolean tieneEspecial = password.matches(".*[!@#$%^&*?_].*");
        boolean sinEspacios = !password.contains(" ");

        return longitudValida && tieneMayuscula && tieneMinuscula
                && tieneNumero && tieneEspecial && sinEspacios;
    }
}
