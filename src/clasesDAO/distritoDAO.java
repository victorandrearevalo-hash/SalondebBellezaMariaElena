package clasesDAO;

import clases.Distrito;
import conexionSql.Conexion;
import interfaces.Interfaz;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class distritoDAO implements Interfaz<Distrito> {

    @Override
    public List<Distrito> listar() {
        List<Distrito> lista = new ArrayList<>();
        String sql = "{CALL sp_listar_distrito()}";

        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Distrito dto = new Distrito();
                dto.setIdDistrito(rs.getInt("Id_Distrito"));
                dto.setNombre(rs.getString("Nombre_Distrito"));
                dto.setCodigoPostal(rs.getString("Codigo_Postal"));
                lista.add(dto);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar distritos: " + e.getMessage());
        }
        return lista;
    }
}
