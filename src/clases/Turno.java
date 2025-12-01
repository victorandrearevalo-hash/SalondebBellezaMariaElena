package clases;

/**
 *
 * @author Administrador
 */
public class Turno {

    private int idTurno;
    private String nombreTurno;
    private String horaInicio;
    private String horaFin;

    public Turno() {
    }

    public Turno(int idTurno, String nombreTurno, String horaInicio, String horaFin) {
        this.idTurno = idTurno;
        this.nombreTurno = nombreTurno;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getNombreTurno() {
        return nombreTurno;
    }

    public void setNombreTurno(String nombreTurno) {
        this.nombreTurno = nombreTurno;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return nombreTurno; // para mostrar solo el nombre
    }

}
