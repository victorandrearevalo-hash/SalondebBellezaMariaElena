package clases;

/**
 *
 * @author Administrador
 */
public class Area {

    private int idArea;
    private String nombreArea;

    public Area() {
    }

    public Area(int idArea, String nombre) {
        this.idArea = idArea;
        this.nombreArea = nombre;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombre) {
        this.nombreArea = nombre;
    }

    @Override
    public String toString() {
        return nombreArea != null ? nombreArea : "----Seleccione un área----";
    }

}
