package clases;

import java.time.LocalDate;

/**
 *
 * @author Administrador
 */
public class DashboardCitasPorDia {

    private String diaSemana;
    private LocalDate fecha;
    private int totalCitas;
    private int citasCanceladas;
    private int citasCompletadas;

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getTotalCitas() {
        return totalCitas;
    }

    public void setTotalCitas(int totalCitas) {
        this.totalCitas = totalCitas;
    }

    public int getCitasCanceladas() {
        return citasCanceladas;
    }

    public void setCitasCanceladas(int citasCanceladas) {
        this.citasCanceladas = citasCanceladas;
    }

    public int getCitasCompletadas() {
        return citasCompletadas;
    }

    public void setCitasCompletadas(int citasCompletadas) {
        this.citasCompletadas = citasCompletadas;
    }
}
