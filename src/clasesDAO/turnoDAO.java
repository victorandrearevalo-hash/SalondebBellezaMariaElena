package clasesDAO;

import clases.Turno;
import conexionSql.Conexion;
import interfaces.Interfaz;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class turnoDAO implements Interfaz<Turno> {

    @Override
    public List<Turno> listar() {
        List<Turno> lista = new ArrayList<>();
        String sql = "{CALL sp_listar_turnos()}";

        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Turno turno = new Turno(
                        rs.getInt("Id_Turno"),
                        rs.getString("Nombre_Turno"),
                        rs.getString("Hora_Inicio"),
                        rs.getString("Hora_Fin")
                );
                lista.add(turno);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar turnos: " + e.getMessage());
        }

        return lista;
    }
}
