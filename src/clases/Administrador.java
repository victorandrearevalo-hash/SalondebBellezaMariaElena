package clases;

/**
 *
 * @author Administrador
 */
public class Administrador extends Usuario {

    public enum Tipo {
        Completo, Limitado
    }
    private Tipo tipo; // COMPLETO o LIMITADO

    public Administrador() {
    }

    // Constructor que asigna el tipo
    public Administrador(int id, String nombre, String rol, String contrasena, Tipo tipo) {
        super.setId(id);
        super.setNombre(nombre);
        super.setRol(rol);
        super.setContrasena(contrasena);
        this.tipo = tipo;
    }

    // Getter y Setter del tipo
    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Administrador{" + super.toString() + ", tipo=" + tipo + "}";
    }

    // Convertir el string de la base de datos en Tipo
    public static Administrador.Tipo tipoDesdeString(String rolUsuario) {
        if (rolUsuario == null) {
            return Tipo.Limitado; // default
        }
        rolUsuario = rolUsuario.trim().toLowerCase();
        if (rolUsuario.contains("completo")) {
            return Tipo.Completo;
        } else {
            return Tipo.Limitado;
        }
    }
}
