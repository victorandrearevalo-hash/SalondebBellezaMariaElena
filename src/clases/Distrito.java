package clases;

/**
 *
 * @author Administrador
 */
public class Distrito {

    private int idDistrito;
    private String nombre;
    private String codPostal;

    public Distrito() {
    }

    public Distrito(int idDistrito, String nombre) {
    this.idDistrito = idDistrito;
    this.nombre = nombre;
}



    public int getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(int idDistrito) {
        this.idDistrito = idDistrito;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoPostal() {
        return codPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codPostal = codigoPostal;
    }

    @Override
    public String toString() {
        return nombre; // útil para mostrarlo en JComboBox
    }

}
