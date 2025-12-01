package clases;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author Administrador
 */
public class Cita {

    private int idCita;
    private Date fecha;
    private Time hora;
    private String estado;
    private String observacion;
    private int idCliente;
    private int idTrabajador;
    private int idUsuario;
    private int idServicio;
    private int idArea;

    // Campos opcionales para mostrar en JTable
    private String nombreCliente;
    private String nombreTrabajador;
    private String nombreUsuario;
    private String nombreServicio;
    private String nombreArea;

    public Cita() {
    }

    public Cita(int idCita, Date fechaCita, Time horaCita, String estadoCita,
            String observacionCita, int idCliente, int idTrabajador, int idUsuario, int idServicio) {
        this.idCita = idCita;
        this.fecha = fechaCita;
        this.hora = horaCita;
        this.estado = estadoCita;
        this.observacion = observacionCita;
        this.idCliente = idCliente;
        this.idTrabajador = idTrabajador;
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public Date getFechaCita() {
        return fecha;
    }

    public void setFechaCita(Date fechaCita) {
        this.fecha = fechaCita;
    }

    public Time getHoraCita() {
        return hora;
    }

    public void setHoraCita(Time horaCita) {
        this.hora = horaCita;
    }

    public String getEstadoCita() {
        return estado;
    }

    public void setEstadoCita(String estadoCita) {
        this.estado = estadoCita;
    }

    public String getObservacionCita() {
        return observacion;
    }

    public void setObservacionCita(String observacionCita) {
        this.observacion = observacionCita;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

///////////////////////////////////////////////////////////////////////////
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    @Override
    public String toString() {
        return "Cita{"
                + "idCita=" + idCita
                + ", fecha=" + fecha
                + ", hora=" + hora
                + ", estado='" + estado + '\''
                + ", observacion='" + observacion + '\''
                + ", cliente='" + nombreCliente + '\''
                + ", trabajador='" + nombreTrabajador + '\''
                + ", servicio='" + nombreServicio + '\''
                + ", usuario='" + nombreUsuario + '\''
                + ", area='" + nombreArea + '\''
                + '}';
    }

}
