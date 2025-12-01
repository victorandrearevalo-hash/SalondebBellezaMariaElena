package clases;

/**
 *
 * @author Administrador
 */
public class Servicio {

    private int idServicio;
    private String nombre;
    private double precio;
    private int duracion;
    private Area area;   // 🔥 Ahora el servicio tiene un objeto Area real


    // Constructor vacío
    public Servicio() {
    }

    // Constructor con parámetros (para usar en combos)
    public Servicio(int idServicio, String nombre) {
        this.idServicio = idServicio;
        this.nombre = nombre;
    }

    // Constructor completo
    public Servicio(int idServicio, String nombre, double precio,
            int duracion, Area area) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.precio = precio;
        this.duracion = duracion;
        this.area = area;
    }

    // Getters y Setters
    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    
  
    @Override
    public String toString() {
        return nombre != null ? nombre : "----Seleccione un servicio----";
    }

}
