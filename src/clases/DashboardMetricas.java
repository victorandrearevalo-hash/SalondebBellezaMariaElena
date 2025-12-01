package clases;

/**
 *
 * @author Administrador
 */
public class DashboardMetricas {

    private int totalCitas;
    private int citasHoy;
    private int citasAtendidas;
    private int citasPendientes;
    private int citasCanceladas;
    private int clientesActivos;
    private int trabajadoresActivos;
    private String servicioMasSolicitado;
    private String areaMasDemandada;
    private double promedioProductividad;

    // Getters y Setters
    public int getTotalCitas() {
        return totalCitas;
    }

    public void setTotalCitas(int totalCitas) {
        this.totalCitas = totalCitas;
    }

    public int getCitasHoy() {
        return citasHoy;
    }

    public void setCitasHoy(int citasHoy) {
        this.citasHoy = citasHoy;
    }

    public int getCitasAtendidas() {
        return citasAtendidas;
    }

    public void setCitasAtendidas(int citasAtendidas) {
        this.citasAtendidas = citasAtendidas;
    }

    public int getCitasPendientes() {
        return citasPendientes;
    }

    public void setCitasPendientes(int citasPendientes) {
        this.citasPendientes = citasPendientes;
    }

    public int getCitasCanceladas() {
        return citasCanceladas;
    }

    public void setCitasCanceladas(int citasCanceladas) {
        this.citasCanceladas = citasCanceladas;
    }

    public int getClientesActivos() {
        return clientesActivos;
    }

    public void setClientesActivos(int clientesActivos) {
        this.clientesActivos = clientesActivos;
    }

    public int getTrabajadoresActivos() {
        return trabajadoresActivos;
    }

    public void setTrabajadoresActivos(int trabajadoresActivos) {
        this.trabajadoresActivos = trabajadoresActivos;
    }

    public String getServicioMasSolicitado() {
        return servicioMasSolicitado;
    }

    public void setServicioMasSolicitado(String servicioMasSolicitado) {
        this.servicioMasSolicitado = servicioMasSolicitado;
    }

    public String getAreaMasDemandada() {
        return areaMasDemandada;
    }

    public void setAreaMasDemandada(String areaMasDemandada) {
        this.areaMasDemandada = areaMasDemandada;
    }

    public double getPromedioProductividad() {
        return promedioProductividad;
    }

    public void setPromedioProductividad(double promedioProductividad) {
        this.promedioProductividad = promedioProductividad;
    }

}
