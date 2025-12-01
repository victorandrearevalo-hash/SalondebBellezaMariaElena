package clases;

/**
 *
 * @author Administrador
 */
public class Recepcionista extends Usuario {

    public Recepcionista() {
    }

    public Recepcionista(int id, String nombre, String rol, String contrasena) {
        super.setId(id);
        super.setNombre(nombre);
        super.setRol(rol);
        super.setContrasena(contrasena);
    }

    @Override
    public String toString() {
        return "Recepcionista{" + super.toString() + "}";
    }
}
