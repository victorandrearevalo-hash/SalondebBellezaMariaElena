/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testConexionSql;

import conexionSql.Conexion;

/**
 *
 * @author Administrador
 */
public class logeo {

    public static void main(String[] args) {
        Conexion con = new Conexion();
        if (con.getConexion() != null) {
            System.out.println("✅ Conexión exitosa a la base de datos.");
        } else {
            System.out.println("❌ Error al conectar con la base de datos.");
        }
    }
}
