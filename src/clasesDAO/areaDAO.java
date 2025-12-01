package clasesDAO;

import clases.Area;
import clases.Servicio;
import conexionSql.Conexion;
import interfaces.ICreate;
import interfaces.IUpdate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class areaDAO implements ICreate<Area>, IUpdate<Area> {

    private String ultimoMensaje;
    private int ultimoTipo;  // 0=ERROR, 1=ADVERTENCIA, 2=EXITO

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public int getUltimoTipo() {
        return ultimoTipo;
    }

    @Override
    public boolean insertar(Area a) {
        boolean exito = false;
        try (Connection cn = Conexion.getConexion(); CallableStatement cs = cn.prepareCall("{CALL sp_registrarArea(?, ?)}")) {

            cs.setString(1, a.getNombreArea());
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();

            int resultado = cs.getInt(2);
            switch (resultado) {
                case -1:
                    ultimoMensaje = "⚠️ El nombre del área ya existe.";
                    ultimoTipo = 1;
                    break;
                case 1:
                    ultimoMensaje = "✔️ Área registrada correctamente.";
                    ultimoTipo = 2;
                    exito = true;
                    break;
                case -99:
                    ultimoMensaje = "❌ Error general en el procedimiento almacenado.";
                    ultimoTipo = 0;
                    return false;
                default:
                    ultimoMensaje = "⚠️ Error desconocido al registrar área.";
                    ultimoTipo = 1;
                    break;
            }

        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al insertar área: " + e.getMessage();
            ultimoTipo = 0;
        }
        return exito;
    }

    @Override
    public boolean actualizar(Area a) {
        String sql = "{CALL sp_actualizarArea(?, ?, ?)}"; // IdArea, NombreArea, Resultado
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, a.getIdArea());          // Id del área
            cs.setString(2, a.getNombreArea());   // Nuevo nombre
            cs.registerOutParameter(3, Types.INTEGER); // @Resultado

            cs.execute();

            int resultado = cs.getInt(3);

            switch (resultado) {
                case -1:
                    ultimoMensaje = "⚠️ El nombre del área ya existe.";
                    ultimoTipo = 1;
                    return false;
                case -99:
                    ultimoMensaje = "❌ Error general en el procedimiento almacenado.";
                    ultimoTipo = 0;
                    return false;
                case 1:
                    ultimoMensaje = "✔️ Área actualizada correctamente.";
                    ultimoTipo = 2;
                    return true;
                default:
                    ultimoMensaje = "⚠️ Resultado desconocido del SP: " + resultado;
                    ultimoTipo = 1;
                    return false;
            }

        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al actualizar área: " + e.getMessage();
            ultimoTipo = 0;
            return false;
        }
    }

    public List<Area> listarAreas() {
        List<Area> lista = new ArrayList<>();
        String sql = "{CALL sp_listar_areas()}";

        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Area area = new Area(
                        rs.getInt("Id_Area"),
                        rs.getString("Nombre_Area")
                );
                lista.add(area);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar áreas: " + e.getMessage());
        }

        return lista;
    }

    public ArrayList<Servicio> listarServicio() {
        ArrayList<Servicio> lista = new ArrayList<>();
        String sql = "{CALL sp_listarAreasServicios()}";

        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {

                Servicio s = new Servicio();
                s.setIdServicio(rs.getInt("Id_Servicio"));
                s.setNombre(rs.getString("Nombre_Servicio"));
                s.setPrecio(rs.getDouble("Precio_Servicio"));
                s.setDuracion(rs.getInt("Duracion_Minutos"));

                // Crear objeto Area y asignarlo al Servicio
                Area a = new Area();
                a.setIdArea(rs.getInt("Id_Area"));
                a.setNombreArea(rs.getString("Nombre_Area"));
                s.setArea(a);

                lista.add(s);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar áreas y servicios: " + e.getMessage());
        }

        return lista;
    }

}
