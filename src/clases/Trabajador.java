package clases;


/**
 *
 * @author Administrador
 */
public class Trabajador {

    private int id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String horario;
    private int idArea;
    private boolean estado;

    private String CodArea;

    public Trabajador() {
    }

    public Trabajador(int id, String nombreCompleto) {
        this.id = id;
        this.nombres = nombreCompleto;
    }

    public Trabajador(int id, String nombres, String apellidos) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        if (nombres == null && apellidos == null) {
            return "----Seleccione Trabajador----";
        }
        return nombres + (apellidos != null ? " " + apellidos : "");
    }

    public String getCodArea() {
        return CodArea;
    }

    public void setCodArea(String CodArea) {
        this.CodArea = CodArea;
    }

}
