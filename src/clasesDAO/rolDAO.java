package clasesDAO;

import clases.Rol;
import interfaces.IRead;
import conexionSql.Conexion;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class rolDAO implements IRead<Rol> {

    @Override
    public ArrayList<Rol> listar() {
        ArrayList<Rol> lista = new ArrayList<>();

        String sql = "{CALL sp_listar_roles}";

        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            // Primera opción del combo
            lista.add(new Rol(0, "----------- Seleccionar -----------"));

            while (rs.next()) {
                int id = rs.getInt("Id_Rol");
                String nombre = rs.getString("Nombre_Rol");

                lista.add(new Rol(id, nombre));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar roles: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public ArrayList<Rol> buscar(String criterio) {
        return null; // por ahora no lo usas
    }

}
