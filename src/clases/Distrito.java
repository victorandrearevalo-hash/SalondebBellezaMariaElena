/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Administrador
 */
public class Distrito {

    private String idDistrito;
    private String nombreDistrito;
    private String codigoPostal;

    public Distrito() {
    }

    public Distrito(String idDistrito, String nombreDistrito, String codigoPostal) {
        this.idDistrito = idDistrito;
        this.nombreDistrito = nombreDistrito;
        this.codigoPostal = codigoPostal;
    }

    public String getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(String idDistrito) {
        this.idDistrito = idDistrito;
    }

    public String getNombreDistrito() {
        return nombreDistrito;
    }

    public void setNombreDistrito(String nombreDistrito) {
        this.nombreDistrito = nombreDistrito;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    @Override
    public String toString() {
        return nombreDistrito; // útil para mostrarlo en JComboBox
    }
}
